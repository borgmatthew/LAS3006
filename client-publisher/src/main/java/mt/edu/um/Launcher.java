package mt.edu.um;

import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
import mt.edu.um.protocol.message.ConnectMessage;
import mt.edu.um.protocol.message.MessageFactory;
import mt.edu.um.protocol.message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by matthew on 16/12/2015.
 */
public class Launcher {

    public static void main(String[] args) {
        final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();

        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {

            socketChannel.configureBlocking(false);
            System.out.println("Trying to connect to server...");

            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            System.out.println("Connection result: " + socketChannel.connect(new InetSocketAddress("127.0.0.1", 3523)));

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
                            key.interestOps(SelectionKey.OP_WRITE);
                        }

                        if (key.isValid() && key.isReadable()) {
                            System.out.println("key is readable");
                            ByteBuffer header = ByteBuffer.allocate(128);
                            int read = ((SocketChannel) key.channel()).read(header);
                            System.out.println("Received: " + read + " bytes.");
                            if (read == -1) {
                                key.channel().close();
                            }
                        }

                        if (key.isValid() && key.isWritable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ConnectMessage connectMessage = (ConnectMessage) MessageFactory.getMessageInstance(MessageType.CONNECT);
                            connectMessage.setId(1);
                            if(messages < 3) {
                                brokerProtocol.send(channel, connectMessage);
                                messages++;
                            }
                            Thread.sleep(5000);
                        }

                        if (!key.isValid()) {
                            System.out.println("Key is not valid");
                            SocketChannel channel = (SocketChannel) key.channel();
                            key.cancel();
                            channel.close();
                            System.out.println("Closed channel");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
