package mt.edu.um.core;

import mt.edu.um.connection.Connection;
import mt.edu.um.connection.ConnectionState;
import mt.edu.um.protocol.message.*;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.subscriber.SubscribersFacade;
import mt.edu.um.subscriber.SubscribersFacadeImpl;
import mt.edu.um.topic.TopicPath;
import mt.edu.um.topic.TopicsFacade;
import mt.edu.um.topic.TopicsFacadeImpl;
import mt.edu.um.topictree.TopicTreeFacade;
import mt.edu.um.topictree.TopicTreeFacadeImpl;
import mt.edu.um.topictree.TopicTreeImpl;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by matthew on 02/01/2016.
 */
public class MessageHandler {

    private final ExecutorService executorService;
    private final TopicTreeFacade topicTreeFacade = new TopicTreeFacadeImpl(new TopicTreeImpl());
    private final SubscribersFacade subscribersFacade = new SubscribersFacadeImpl();
    private final TopicsFacade topicsFacade = new TopicsFacadeImpl();
    private final SubscriberConnections subscriberConnections = new SubscriberConnections();

    public MessageHandler() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void handleMessage(Message message, Connection origin) {
        switch (message.getType()) {
            case CONNECT: {
                handle((ConnectMessage) message, origin);
                break;
            }
            case SUBSCRIBE: {
                handle((SubscribeMessage) message, origin);
                break;
            }
            case PINGREQ: {
                handle((PingReqMessage) message, origin);
                break;
            }
            case PUBLISH: {
                handle((PublishMessage) message, origin);
                break;
            }
            case PUBREC: {
                handle((PubRecMessage) message, origin);
                break;
            }
            case UNSUBSCRIBE: {
                handle((UnsubscribeMessage) message, origin);
                break;
            }
            case DISCONNECT: {
                handle((DisconnectMessage) message, origin);
                break;
            }
        }
    }

    private void handle(ConnectMessage connectMessage, Connection origin) {
        System.out.println(connectMessage.getType() + ": " + connectMessage.getId());
        CompletableFuture.supplyAsync(() -> {
            boolean result = subscribersFacade.subscribe(connectMessage.getId());
            ConnAckMessage reply = ((ConnAckMessage) MessageFactory.getMessageInstance(MessageType.CONNACK))
                    .setId(connectMessage.getId())
                    .setResult(result);
            origin.getOutgoingMessages().add(reply);
            origin.getSelectionKey().interestOps(origin.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
            origin.getSelectionKey().selector().wakeup();
            return reply;
        }, executorService)
                .thenAccept(reply -> {
                    if (reply.getResult()) {
                        origin.setState(ConnectionState.CONNECTED);
                        origin.setSubscriberId(connectMessage.getId());
                        subscriberConnections.add(connectMessage.getId(), origin);
                    }
                });
    }

    private void handle(SubscribeMessage subscribeMessage, Connection origin) {
        System.out.println(subscribeMessage.getType() + ": " + subscribeMessage.getTopic());
        CompletableFuture.supplyAsync(() -> {
            TopicPath path = topicsFacade.convertToTopicPath(subscribeMessage.getTopic());
            Optional<Subscriber> subscriberOptional = subscribersFacade.get(origin.getSubscriberId());
            boolean result = topicTreeFacade.subscribe(path, new HashSet<>(Arrays.asList(subscriberOptional.get())));
            SubAckMessage reply = ((SubAckMessage) MessageFactory.getMessageInstance(MessageType.SUBACK))
                    .setTopic(subscribeMessage.getTopic())
                    .setResult(result);
            origin.getOutgoingMessages().add(reply);
            origin.getSelectionKey().interestOps(origin.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
            origin.getSelectionKey().selector().wakeup();
            return reply;
        }, executorService);
    }

    private void handle(PingReqMessage pingReqMessage, Connection origin) {
        System.out.println(pingReqMessage.getType() + ": " + pingReqMessage.getMessageId());
        CompletableFuture.runAsync(() -> {
            subscribersFacade.update(origin.getSubscriberId(), LocalDateTime.now());
            PingRespMessage reply = ((PingRespMessage) MessageFactory.getMessageInstance(MessageType.PINGRESP))
                    .setMessageId(pingReqMessage.getMessageId());
            origin.getOutgoingMessages().add(reply);
            origin.getSelectionKey().interestOps(origin.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
            origin.getSelectionKey().selector().wakeup();
        }, executorService);
    }

    private void handle(PublishMessage publishMessage, Connection origin) {
        System.out.println(publishMessage.getType() + ": " + publishMessage.getTopic()
                + "\nMESSAGE: " + publishMessage.getMessageId()
                + "\nPAYLOAD: " + publishMessage.getPayload());
        CompletableFuture.runAsync(() -> {
            TopicPath path = topicsFacade.convertToTopicPath(publishMessage.getTopic());
            Set<Subscriber> subscribers = topicTreeFacade.getSubscribers(path);
            PubAckMessage pubAckMessage = ((PubAckMessage) MessageFactory.getMessageInstance(MessageType.PUBACK))
                    .setTopic(publishMessage.getTopic())
                    .setMessageId(publishMessage.getMessageId());
            subscribers.stream()
                    .forEach(subscriber -> {
                        Connection connection = subscriberConnections.get(subscriber.getId());
                        connection.getOutgoingMessages().add(publishMessage);
                        connection.getSelectionKey().interestOps(connection.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
                    });
            origin.getOutgoingMessages().add(pubAckMessage);
            origin.getSelectionKey().interestOps(origin.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
            origin.getSelectionKey().selector().wakeup();
        }, executorService);
    }

    private void handle(PubRecMessage pubRecMessage, Connection origin) {
    }

    private void handle(UnsubscribeMessage unsubscribeMessage, Connection origin) {
        System.out.println(unsubscribeMessage.getType() + ": " + unsubscribeMessage.getTopic());
        CompletableFuture.supplyAsync(() -> {
            TopicPath path = topicsFacade.convertToTopicPath(unsubscribeMessage.getTopic());
            Optional<Subscriber> subscriberOptional = subscribersFacade.get(origin.getSubscriberId());
            boolean result;
            if (subscriberOptional.isPresent()) {
                result = topicTreeFacade.unsubscribe(path, subscriberOptional.get());
            } else {
                result = false;
            }
            UnsubAckMessage unsubAckMessage = ((UnsubAckMessage) MessageFactory.getMessageInstance(MessageType.UNSUBACK))
                    .setTopic(unsubscribeMessage.getTopic())
                    .setResult(result);
            origin.getOutgoingMessages().add(unsubscribeMessage);
            origin.getSelectionKey().interestOps(origin.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
            origin.getSelectionKey().selector().wakeup();
            return unsubAckMessage;
        }, executorService);
    }

    private void handle(DisconnectMessage disconnectMessage, Connection origin) {
        System.out.println(disconnectMessage.getType());
        closeConnection(origin);
    }

    public void closeConnection(Connection connection) {
        CompletableFuture.runAsync(() -> {
            subscribersFacade.unsubscribe(connection.getSubscriberId());
            //TODO: unsubscribe this subscriber from every topic
        }, executorService);
        subscriberConnections.remove(connection.getSubscriberId());
        try {
            connection.getSelectionKey().channel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
