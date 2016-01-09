package mt.edu.um;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.connection.ConnectionState;
import mt.edu.um.protocol.message.*;

import java.util.Optional;
import java.util.Random;

/**
 * Created by matthew on 09/01/2016.
 */
public class PublisherMessageGenerator implements MessageGenerator {

    private final String publishTopic;

    public PublisherMessageGenerator(String publishTopic) {
        this.publishTopic = publishTopic;
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
            PublishMessage publishMessage = generatePublishMessage();
            return Optional.of(publishMessage);
        }
    }

    private PublishMessage generatePublishMessage() {
        PublishMessage publishMessage = (PublishMessage) MessageFactory.getMessageInstance(MessageType.PUBLISH);
        publishMessage.setTopic(publishTopic)
                .setMessageId(new Random().nextInt())
                .setPayload(generateRandomSentence());
        return publishMessage;
    }

    private String generateRandomSentence() {
        return "random sentence";
    }

    private ConnectMessage generateConnectMessage() {
        ConnectMessage connectMessage = (ConnectMessage) MessageFactory.getMessageInstance(MessageType.CONNECT);
        connectMessage.setId(new Random().nextInt());
        return connectMessage;
    }
}
