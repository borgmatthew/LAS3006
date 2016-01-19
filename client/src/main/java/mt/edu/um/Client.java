package mt.edu.um;

import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
import mt.edu.um.protocol.connection.Connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;

/**
 * Created by matthew on 16/12/2015.
 */
public class Client {

    private final MessageGenerator messageGenerator;
    private final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();
    private final int messageFrequencyInSeconds;

    public Client(final MessageGenerator messageGenerator, final int messageFrequencyInSeconds) {
        this.messageGenerator = messageGenerator;
        this.messageFrequencyInSeconds = messageFrequencyInSeconds;
    }

    public void run(final String serverIp, final int serverPort) {
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {

            socketChannel.configureBlocking(false);

            SelectionKey registeredKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(serverIp, serverPort));

            final Connection serverConnection = new Connection(registeredKey);
            registeredKey.attach(serverConnection);

            ScheduledMessageGenerator scheduledMessageGenerator = new ScheduledMessageGenerator(messageGenerator, serverConnection);
            final Timer timer = new Timer();
            timer.schedule(scheduledMessageGenerator, 0L, (long)messageFrequencyInSeconds * 1000L);

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
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (key.isValid() && key.isReadable()) {
                            try {
                                brokerProtocol.receive((SocketChannel) key.channel()).stream()
                                        .forEach(message -> message.accept(new ClientMessageHandler((Connection) key.attachment())));
                            } catch (IOException e) {
                                key.cancel();
                            }
                        }

                        if (key.isValid() && key.isWritable()) {
                            Connection connection = (Connection) key.attachment();
                            SocketChannel channel = (SocketChannel) key.channel();
                            connection.getOutgoingMessages().emptyBuffer().stream().forEach(message -> {
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
                            timer.cancel();
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
