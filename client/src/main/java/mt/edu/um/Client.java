package mt.edu.um;

import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * Created by matthew on 16/12/2015.
 */
public class Client {

    private final MessageGenerator messageGenerator;
    private final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();

    public Client(final MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    public void run(final String serverIp, final int serverPort) {
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {

            socketChannel.configureBlocking(false);

            SelectionKey registeredKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(serverIp, serverPort));

            final Connection serverConnection = new Connection(registeredKey);
            registeredKey.attach(serverConnection);

            boolean shutdown = false;
            while (!shutdown) {
                if (selector.select(5000) > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isValid() && key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.finishConnect();
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (key.isValid() && key.isReadable()) {
                            try {
                                brokerProtocol.receive((SocketChannel) key.channel())
                                        .forEach(message -> message.accept(new ClientMessageHandler((Connection) key.attachment())));
                            } catch (IOException e) {
                                key.cancel();
                            }
                        }

                        if (key.isValid() && key.isWritable()) {
                            Connection connection = (Connection) key.attachment();
                            SocketChannel channel = (SocketChannel) key.channel();
                            connection.getOutgoingMessages().emptyBuffer().forEach(message -> {
                                try {
                                    brokerProtocol.send(channel, message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (!key.isValid()) {
                            System.out.println("Closing connection.\nBye.");
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.close();
                            shutdown = true;
                        }
                    }
                } else {
                    Optional<Message> generatedMessage = messageGenerator.generate(serverConnection);
                    if (generatedMessage.isPresent()) {
                        serverConnection.getOutgoingMessages().add(generatedMessage.get());
                        serverConnection.getSelectionKey().interestOps(serverConnection.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
                        serverConnection.getSelectionKey().selector().wakeup();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
