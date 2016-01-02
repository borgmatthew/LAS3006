package mt.edu.um.core;

import mt.edu.um.connection.Connection;
import mt.edu.um.connection.ConnectionState;
import mt.edu.um.protocol.message.*;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.subscriber.SubscribersFacade;
import mt.edu.um.topic.TopicPath;
import mt.edu.um.topic.TopicsFacade;
import mt.edu.um.topictree.TopicTreeFacade;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Created by matthew on 28/12/2015.
 */
public class ServerMessageHandlerVisitor implements Visitor {

    private final SubscriberConnections subscriberConnections;
    private final SubscribersFacade subscribersFacade;
    private final TopicTreeFacade topicTreeFacade;
    private final TopicsFacade topicsFacade;
    private final Connection origin;
    private final ExecutorService executorService;
    private final List<Response> responses;

    public ServerMessageHandlerVisitor(SubscribersFacade subscribersFacade, TopicTreeFacade topicTreeFacade, TopicsFacade topicsFacade, SubscriberConnections subscriberConnections, Connection origin, ExecutorService executorService, final List<Response> responses) {
        this.subscribersFacade = subscribersFacade;
        this.topicTreeFacade = topicTreeFacade;
        this.topicsFacade = topicsFacade;
        this.subscriberConnections = subscriberConnections;
        this.origin = origin;
        this.executorService = executorService;
        this.responses = responses;
    }

    @Override
    public void visit(ConnectMessage connectMessage) {
        System.out.println("Received connection. Client id: " + connectMessage.getId());
        CompletableFuture.supplyAsync(() -> subscribersFacade.subscribe(connectMessage.getId()), executorService)
                .thenAccept(result -> {
                    ConnAckMessage reply = ((ConnAckMessage) MessageFactory.getMessageInstance(MessageType.CONNACK))
                            .setId(connectMessage.getId())
                            .setResult(result);
                    responses.add(new Response(reply, origin));
                    origin.setState(ConnectionState.CONNECTED);
                    origin.setSubscriberId(connectMessage.getId());
                    subscriberConnections.add(connectMessage.getId(), origin);
                });
    }

    @Override
    public void visit(ConnAckMessage connAckMessage) {

    }

    @Override
    public void visit(SubscribeMessage subscribeMessage) {
        System.out.println("Received subscribe message.");
        TopicPath path = topicsFacade.convertToTopicPath(subscribeMessage.getTopic());
        Optional<Subscriber> subscriberOptional = subscribersFacade.get(origin.getSubscriberId());
        CompletableFuture.supplyAsync(() -> topicTreeFacade.subscribe(path, new HashSet<Subscriber>(Arrays.asList(subscriberOptional.get()))), executorService)
                .thenAccept(result -> {
                    SubAckMessage reply = ((SubAckMessage) MessageFactory.getMessageInstance(MessageType.SUBACK))
                            .setTopic(subscribeMessage.getTopic())
                            .setResult(result);
                    responses.add(new Response(reply, origin));
                });
    }

    @Override
    public void visit(SubAckMessage subAckMessage) {

    }

    @Override
    public void visit(PingReqMessage pingReqMessage) {

    }

    @Override
    public void visit(PingRespMessage pingRespMessage) {

    }

    @Override
    public void visit(PublishMessage publishMessage) {

    }

    @Override
    public void visit(PubAckMessage pubAckMessage) {

    }

    @Override
    public void visit(PubRecMessage pubRecMessage) {

    }

    @Override
    public void visit(UnsubscribeMessage unsubscribeMessage) {

    }

    @Override
    public void visit(UnsubAckMessage unsubAckMessage) {

    }

    @Override
    public void visit(DisconnectMessage disconnectMessage) {

    }
}
