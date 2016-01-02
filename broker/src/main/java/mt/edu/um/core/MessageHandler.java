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

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by matthew on 02/01/2016.
 */
public class MessageHandler {

    private final ExecutorService executorService;
    private final TopicTreeFacade topicTreeFacade = new TopicTreeFacadeImpl(new TopicTreeImpl());
    private final SubscribersFacade subscribersFacade = new SubscribersFacadeImpl();
    private final TopicsFacade topicsFacade = new TopicsFacadeImpl();
    private final SubscriberConnections subscriberConnections = new SubscriberConnections();
    private final List<Response> responses;

    public MessageHandler() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.responses = new ArrayList<>();
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
//        message.accept(new ServerMessageHandlerVisitor(subscribersFacade, topicTreeFacade, topicsFacade, subscriberConnections, origin, executorService, responses));
    }

    public List<Response> getResponses() {
        return this.responses;
    }

    private void handle(ConnectMessage connectMessage, Connection origin) {
        CompletableFuture.supplyAsync(() -> {
            boolean result = subscribersFacade.subscribe(connectMessage.getId());
            ConnAckMessage reply = ((ConnAckMessage) MessageFactory.getMessageInstance(MessageType.CONNACK))
                    .setId(connectMessage.getId())
                    .setResult(result);
            return reply;
        }, executorService)
                .thenAccept(reply -> {
                    responses.add(new Response(reply, origin));
                    if (reply.getResult()) {
                        origin.setState(ConnectionState.CONNECTED);
                        origin.setSubscriberId(connectMessage.getId());
                        subscriberConnections.add(connectMessage.getId(), origin);
                    }
                });
    }

    private void handle(SubscribeMessage subscribeMessage, Connection origin) {
        CompletableFuture.supplyAsync(() -> {
            TopicPath path = topicsFacade.convertToTopicPath(subscribeMessage.getTopic());
            Optional<Subscriber> subscriberOptional = subscribersFacade.get(origin.getSubscriberId());
            boolean result = topicTreeFacade.subscribe(path, new HashSet<>(Arrays.asList(subscriberOptional.get())));
            SubAckMessage reply = ((SubAckMessage) MessageFactory.getMessageInstance(MessageType.SUBACK))
                    .setTopic(subscribeMessage.getTopic())
                    .setResult(result);
            return reply;
        }, executorService)
                .thenAccept(reply -> responses.add(new Response(reply, origin)));
    }

    private void handle(PingReqMessage pingReqMessage, Connection origin) {
        CompletableFuture.supplyAsync(() -> subscribersFacade.update(origin.getSubscriberId(), LocalDateTime.now()), executorService)
                .thenAccept(result -> {
                    PingRespMessage reply = ((PingRespMessage) MessageFactory.getMessageInstance(MessageType.PINGRESP))
                            .setMessageId(pingReqMessage.getMessageId());
                    responses.add(new Response(reply, origin));
                });
    }

    private void handle(PublishMessage publishMessage, Connection origin) {
        CompletableFuture.supplyAsync(() -> {
            TopicPath path = topicsFacade.convertToTopicPath(publishMessage.getTopic());
            Set<Subscriber> subscribers = topicTreeFacade.getSubscribers(path);
            PubAckMessage pubAckMessage = ((PubAckMessage) MessageFactory.getMessageInstance(MessageType.PUBACK))
                    .setTopic(publishMessage.getTopic())
                    .setMessageId(publishMessage.getMessageId());
            List<Response> responses = subscribers.stream()
                    .map(subscriber -> new Response(publishMessage, subscriberConnections.get(subscriber.getId())))
                    .collect(Collectors.toList());
            responses.add(new Response(pubAckMessage, origin));
            return responses;
        }, executorService).thenAccept(this.responses::addAll);
    }

    private void handle(PubRecMessage pubRecMessage, Connection origin) {
    }

    private void handle(UnsubscribeMessage unsubscribeMessage, Connection origin) {
        CompletableFuture.supplyAsync(() -> {
            TopicPath path = topicsFacade.convertToTopicPath(unsubscribeMessage.getTopic());
            Optional<Subscriber> subscriberOptional = subscribersFacade.get(origin.getSubscriberId());
            boolean result;
            if(subscriberOptional.isPresent()) {
                result = topicTreeFacade.unsubscribe(path, subscriberOptional.get());
            } else {
                result = false;
            }
            UnsubAckMessage unsubAckMessage = ((UnsubAckMessage) MessageFactory.getMessageInstance(MessageType.UNSUBACK))
                    .setTopic(unsubscribeMessage.getTopic())
                    .setResult(result);
            return unsubAckMessage;
        }, executorService).thenAccept(message -> this.responses.add(new Response(message, origin)));
    }

    private void handle(DisconnectMessage disconnectMessage, Connection origin) {
        CompletableFuture.runAsync(() -> {
            subscribersFacade.unsubscribe(origin.getSubscriberId());
            //TODO: unsubscribe this subscriber from every topic
        }, executorService);
        subscriberConnections.remove(origin.getSubscriberId());
        origin.getSelectionKey().cancel();
    }

}
