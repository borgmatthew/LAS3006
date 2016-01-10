package mt.edu.um.core;

import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by matthew on 10/12/2015.
 */
public class Server {

    private final EventHandler eventHandler;
    private final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();
    private final int port;
    private final int maxInactiveMinutes;

    public Server(int port, int maxInactiveMinutes) {
        this.port = port;
        this.maxInactiveMinutes = maxInactiveMinutes;
        this.eventHandler = new EventHandler(maxInactiveMinutes);
    }

    public void start() {
        try (Selector selector = Selector.open();
            ServerSocketChannel socketChannel = ServerSocketChannel.open()) {

            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(port));
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                if (selector.select() > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isValid() && key.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                            SocketChannel clientChannel = serverSocketChannel.accept();
                            clientChannel.configureBlocking(false);
                            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                            Connection connection = new Connection(clientKey);
                            clientKey.attach(connection);
                        }

                        if (key.isValid() && key.isReadable()) {
                            try {
                                ((Connection) key.attachment()).getIncomingMessages().addAll(brokerProtocol.receive((SocketChannel) key.channel()));
                                eventHandler.handleMessages((Connection) key.attachment());
                            } catch (IOException e) {
                                key.cancel();
                            }
                        }

                        if (key.isValid() && key.isWritable()) {
                            List<Message> outgoingMessages = ((Connection)key.attachment()).getOutgoingMessages().emptyBuffer();
                            outgoingMessages.forEach(message -> {
                                try {
                                    brokerProtocol.send((SocketChannel) key.channel(), message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (!key.isValid()) {
                            eventHandler.closeConnection((Connection)key.attachment());
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
