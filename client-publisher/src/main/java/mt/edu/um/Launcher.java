package mt.edu.um;

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
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {

            socketChannel.configureBlocking(false);
            System.out.println("Trying to connect to server...");

            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            System.out.println("Connection result: " + socketChannel.connect(new InetSocketAddress("127.0.0.1", 3523)));

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
                            ByteBuffer buffer = ByteBuffer.allocate(20);
                            buffer.put(0, (byte) 'a');
                            System.out.println("Writing to buffer");
                            socketChannel.write(buffer);
                            key.interestOps(SelectionKey.OP_READ);
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

//            while (!socketChannel.finishConnect()) {
//                System.out.println("Connection in progress...");
//            }
//            System.out.println(socketChannel.isConnected());
//            System.out.println("sleeping for 5 sec");
//            Thread.sleep(5000);
//            ByteBuffer buffer = ByteBuffer.allocate(20);
//            buffer.put(0, (byte)'a');
//            System.out.println("Writing to buffer");
//            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
