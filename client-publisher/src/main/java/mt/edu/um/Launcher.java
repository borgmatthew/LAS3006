package mt.edu.um;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by matthew on 16/12/2015.
 */
public class Launcher {

    public static void main(String[] args) {
        try {
            System.out.println("Trying to connect to server...");
            SocketChannel socketChannel = SocketChannel.open();
            System.out.println("Connection result: " + socketChannel.connect(new InetSocketAddress("127.0.0.1", 3522)));

            while (!socketChannel.finishConnect()) {
                System.out.println("Connection in progress...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
