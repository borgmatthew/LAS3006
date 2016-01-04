package mt.edu.um;

import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
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
public class Launcher {

    public static PublishMessage generatePublishMessage() {
        return ((PublishMessage) MessageFactory.getMessageInstance(MessageType.PUBLISH))
                .setMessageId(new Random().nextInt())
                .setTopic("/home/test/kitchen")
                .setPayload("Testing the kitchen");
    }

    public static void main(String[] args) {
        final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();
        final ClientMessageHandler clientMessageHandler = new ClientMessageHandler();
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {

            socketChannel.configureBlocking(false);
            System.out.println("Trying to connect to server...");

            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 3523));

            int messages = 0;

            while (true) {
                if (selector.select() > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isValid() && key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.finishConnect();
                            key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        }

                        if (key.isValid() && key.isReadable()) {
                            brokerProtocol.receive((SocketChannel) key.channel())
                                    .forEach(clientMessageHandler::handleMessage);
//                            key.interestOps(SelectionKey.OP_WRITE);
                        }

                        if (key.isValid() && key.isWritable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            Message message;

                            if (messages < 6) {
                                if (!clientMessageHandler.isConnected()) {
                                    message = ((ConnectMessage) MessageFactory.getMessageInstance(MessageType.CONNECT))
                                            .setId(Math.abs(new Random().nextInt()));
                                    System.out.println("Created connect request with id: " + ((ConnectMessage) message).getId());
                                } else {
                                    message = generatePublishMessage();
                                    System.out.println("Created publish request with id: " + ((PublishMessage) message).getMessageId());
                                }
                                brokerProtocol.send(channel, message);
                                messages++;
//                                key.interestOps(SelectionKey.OP_READ);
                            }
                        }

                        if (!key.isValid()) {
//                            System.out.println("Key is not valid");
//                            SocketChannel channel = (SocketChannel) key.channel();
//                            key.cancel();
//                            channel.close();
//                            System.out.println("Closed channel");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
