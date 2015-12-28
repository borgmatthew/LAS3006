package mt.edu.um.protocol.communication;

import mt.edu.um.protocol.message.Message;

import java.nio.channels.SocketChannel;

/**
 * Created by matthew on 27/12/2015.
 */
public interface BrokerProtocol {

    void send(SocketChannel channel, Message message);

    Message receive(SocketChannel channel);
}
