package mt.edu.um;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.connection.ConnectionState;
import mt.edu.um.protocol.message.*;

import java.util.Optional;
import java.util.Random;

/**
 * Created by matthew on 09/01/2016.
 */
public class SubscriberMessageGenerator implements MessageGenerator {

    private final String subscribeTopic;
    private boolean subscribeMessageSent = false;

    public SubscriberMessageGenerator(String subscribeTopic) {
        this.subscribeTopic = subscribeTopic;
    }

    @Override
    public Optional<Message> generate(Connection connection) {
        if (connection.getState().equals(ConnectionState.NOT_CONNECTED)) {
            ConnectMessage connectMessage = generateConnectMessage();
            connection.setState(ConnectionState.PENDING_CONNECTION);
            connection.setSubscriberId(connectMessage.getId());
            return Optional.of(connectMessage);
        } else if (connection.getState().equals(ConnectionState.PENDING_CONNECTION)) {
            //Connect message is still being processed
            return Optional.empty();
        } else {
            if (!subscribeMessageSent) {
                subscribeMessageSent = true;
                return Optional.of(generateSubscribeMessage());
            } else {
                return Optional.of(generatePingRequest());
            }
        }
    }

    private PingReqMessage generatePingRequest() {
        PingReqMessage pingReqMessage = (PingReqMessage) MessageFactory.getMessageInstance(MessageType.PINGREQ);
        pingReqMessage.setMessageId(new Random().nextInt());
        return pingReqMessage;
    }

    private SubscribeMessage generateSubscribeMessage() {
        SubscribeMessage subscribeMessage = (SubscribeMessage) MessageFactory.getMessageInstance(MessageType.SUBSCRIBE);
        subscribeMessage.setTopic(subscribeTopic);
        return subscribeMessage;
    }

    private ConnectMessage generateConnectMessage() {
        ConnectMessage connectMessage = (ConnectMessage) MessageFactory.getMessageInstance(MessageType.CONNECT);
        connectMessage.setId(new Random().nextInt());
        return connectMessage;
    }
}
