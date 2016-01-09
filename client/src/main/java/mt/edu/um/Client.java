package mt.edu.um;

import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Created by matthew on 16/12/2015.
 */
public class Client {

    public static PublishMessage generatePublishMessage() {
        return ((PublishMessage) MessageFactory.getMessageInstance(MessageType.PUBLISH))
                .setMessageId(new Random().nextInt())
                .setTopic("/home/test/kitchen")
                .setPayload("Testing the kitchen");
    }

    private final MessageGenerator messageGenerator;
    private final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();

    public Client(final MessageGenerator messageGenerator) {
        this.messageGenerator = messageGenerator;
    }

    public void run(final String serverIp, final int serverPort) {
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {

            socketChannel.configureBlocking(false);
            System.out.println("Trying to connect to server...");

            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(serverIp, serverPort));

            boolean shutdown = false;
            while (!shutdown) {
                if (selector.select() > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isValid() && key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.finishConnect();
                            Connection connection = new Connection(key);
                            key.attach(connection);
                            key.interestOps(SelectionKey.OP_WRITE);
                        }

                        if (key.isValid() && key.isReadable()) {
                            brokerProtocol.receive((SocketChannel) key.channel())
                                    .forEach(message -> message.accept(new ClientMessageHandler((Connection)key.attachment())));
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
                        }

                        if (!key.isValid()) {
                            System.out.println("Closing connection.\nBye.");
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.close();
                            shutdown = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
