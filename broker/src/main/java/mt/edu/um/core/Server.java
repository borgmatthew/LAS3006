package mt.edu.um.core;

import mt.edu.um.connection.Connection;
import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by matthew on 10/12/2015.
 */
public class Server {

    private final MessageHandler messageHandler = new MessageHandler();
    private final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();


    public void start() {
        try (Selector selector = Selector.open();
            ServerSocketChannel socketChannel = ServerSocketChannel.open()) {

            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(3523));
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
                            clientChannel.register(selector, SelectionKey.OP_READ);
                            Connection connection = new Connection(key);
                            key.attach(connection);
                            System.out.println("Client accepted!");
                        }

                        if (key.isValid() && key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.finishConnect();
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (key.isValid() && key.isReadable()) {
                            System.out.println("key is readable");
                            try {
                                brokerProtocol.receive((SocketChannel) key.channel()).stream().forEach(message -> messageHandler.handleMessage(message, (Connection) key.attachment()));
                            } catch (IOException e) {
                                key.cancel();
                            }
                        }

                        if (key.isValid() && key.isWritable()) {
                            System.out.println("Key is writable");
                        }

                        if (!key.isValid()) {
                            System.out.println("Key is not valid");
                            SocketChannel channel = (SocketChannel) key.channel();
                            key.cancel();
                            channel.close();
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
