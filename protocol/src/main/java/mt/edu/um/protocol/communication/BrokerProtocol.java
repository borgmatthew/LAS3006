package mt.edu.um.protocol.communication;

import mt.edu.um.protocol.message.Message;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * Created by matthew on 27/12/2015.
 */
public interface BrokerProtocol {

    void send(SocketChannel channel, Message message) throws IOException;

    List<Message> receive(SocketChannel channel) throws IOException;
}
