package mt.edu.um.core;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.connection.ConnectionState;
import mt.edu.um.protocol.message.*;
import mt.edu.um.client.Client;
import mt.edu.um.client.ClientsFacade;
import mt.edu.um.topic.TopicPath;
import mt.edu.um.topic.TopicsFacade;
import mt.edu.um.topictree.TopicTreeFacade;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by matthew on 04/01/2016.
 */
public class MessageHandler implements Visitor {

    private final TopicTreeFacade topicTreeFacade;
    private final ClientsFacade clientsFacade;
    private final TopicsFacade topicsFacade;
    private final ConcurrentHashMap<Integer, Connection> connectionMapper;
    private final Connection origin;

    public MessageHandler(final TopicTreeFacade topicTreeFacade,
                          final ClientsFacade clientsFacade,
                          final TopicsFacade topicsFacade,
                          final  ConcurrentHashMap<Integer, Connection> connectionMapper,
                          final Connection origin) {
        this.topicTreeFacade = topicTreeFacade;
        this.clientsFacade = clientsFacade;
        this.topicsFacade = topicsFacade;
        this.connectionMapper = connectionMapper;
        this.origin = origin;
    }

    @Override
    public void visit(ConnectMessage connectMessage) {
        System.out.println(connectMessage.getType() + ": " + connectMessage.getId() + "\n");
        boolean result = false;

        //Check if client is already connected
        if (origin.getState() == ConnectionState.NOT_CONNECTED) {
            //Create subscriber
            result = clientsFacade.create(connectMessage.getId());
            if (result) {
                //Update connection
                origin.setState(ConnectionState.CONNECTED);
                origin.setClientId(connectMessage.getId());
                connectionMapper.put(connectMessage.getId(), origin);
            }
        }

        ConnAckMessage reply = ((ConnAckMessage) MessageFactory.getMessageInstance(MessageType.CONNACK))
                .setId(connectMessage.getId())
                .setResult(result);
        sendMessage(reply);
    }

    @Override
    public void visit(ConnAckMessage connAckMessage) {

    }

    @Override
    public void visit(SubscribeMessage subscribeMessage) {
        System.out.println(subscribeMessage.getType() + ": " + subscribeMessage.getTopic() + "\n");
        boolean result = false;

        if (origin.getState() == ConnectionState.CONNECTED) {
            Optional<Client> subscriberOptional = clientsFacade.get(origin.getClientId());
            if (subscriberOptional.isPresent()) {
                TopicPath path = topicsFacade.convertToTopicPath(subscribeMessage.getTopic());
                result = topicTreeFacade.subscribe(path, new HashSet<>(Arrays.asList(subscriberOptional.get())));
                if (result) {
                    subscriberOptional.get().getTopics().add(path);
                }
            }
        }

        SubAckMessage reply = ((SubAckMessage) MessageFactory.getMessageInstance(MessageType.SUBACK))
                .setTopic(subscribeMessage.getTopic())
                .setResult(result);
        sendMessage(reply);
    }

    @Override
    public void visit(SubAckMessage subAckMessage) {

    }

    @Override
    public void visit(PingReqMessage pingReqMessage) {
        System.out.println(pingReqMessage.getType() + ": " + pingReqMessage.getMessageId() + "\n");
        PingRespMessage reply = ((PingRespMessage) MessageFactory.getMessageInstance(MessageType.PINGRESP))
                .setMessageId(pingReqMessage.getMessageId());
        sendMessage(reply);
    }

    @Override
    public void visit(PingRespMessage pingRespMessage) {

    }

    @Override
    public void visit(PublishMessage publishMessage) {
        System.out.println(publishMessage.getType() + ": " + publishMessage.getTopic()
                + "\nMESSAGE: " + publishMessage.getMessageId()
                + "\nPAYLOAD: " + publishMessage.getPayload()
                + "\n");
        boolean result = false;

        if (origin.getState() == ConnectionState.CONNECTED) {
            TopicPath path = topicsFacade.convertToTopicPath(publishMessage.getTopic());
            Set<Client> clients = topicTreeFacade.getSubscribers(path);
            clients.stream()
                    .forEach(subscriber -> {
                        Connection connection = connectionMapper.get(subscriber.getId());
                        connection.getOutgoingMessages().add(publishMessage);
                        connection.getSelectionKey().interestOps(connection.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
                    });
            result = true;
        }

        PubAckMessage pubAckMessage = ((PubAckMessage) MessageFactory.getMessageInstance(MessageType.PUBACK))
                .setTopic(publishMessage.getTopic())
                .setMessageId(publishMessage.getMessageId())
                .setResult(result);

        //The selector is interrupted only once when sending the pubAckMessage
        sendMessage(pubAckMessage);
    }

    @Override
    public void visit(PubAckMessage pubAckMessage) {

    }

    @Override
    public void visit(PubRecMessage pubRecMessage) {
        System.out.println(pubRecMessage.getType() + ": " + pubRecMessage.getTopic()
                + "\nCLIENT: " + pubRecMessage.getClientId()
                + "\nMESSAGE: " + pubRecMessage.getMessageId()
                + "\nRESULT: " + (pubRecMessage.getResult() ? "OK" : "ERROR")
                + "\n");
    }

    @Override
    public void visit(UnsubscribeMessage unsubscribeMessage) {
        System.out.println(unsubscribeMessage.getType() + ": " + unsubscribeMessage.getTopic() + "\n");
        boolean result = false;

        if(origin.getState() == ConnectionState.CONNECTED) {
            TopicPath path = topicsFacade.convertToTopicPath(unsubscribeMessage.getTopic());
            Optional<Client> subscriberOptional = clientsFacade.get(origin.getClientId());

            if (subscriberOptional.isPresent()) {
                result = topicTreeFacade.unsubscribe(path, subscriberOptional.get());
                subscriberOptional.get().getTopics().remove(path);
            }
        }

        UnsubAckMessage unsubAckMessage = ((UnsubAckMessage) MessageFactory.getMessageInstance(MessageType.UNSUBACK))
                .setTopic(unsubscribeMessage.getTopic())
                .setResult(result);
        sendMessage(unsubAckMessage);
    }

    @Override
    public void visit(UnsubAckMessage unsubAckMessage) {

    }

    @Override
    public void visit(DisconnectMessage disconnectMessage) {
        System.out.println(disconnectMessage.getType() + "\n");
        origin.getSelectionKey().cancel();
    }

    private void sendMessage(Message reply) {
        origin.getOutgoingMessages().add(reply);
        origin.getSelectionKey().interestOps(origin.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
        origin.getSelectionKey().selector().wakeup();
    }
}
