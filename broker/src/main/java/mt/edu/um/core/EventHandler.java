package mt.edu.um.core;

import mt.edu.um.connection.Connection;
import mt.edu.um.protocol.message.Message;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.subscriber.SubscribersFacade;
import mt.edu.um.subscriber.SubscribersFacadeImpl;
import mt.edu.um.topic.TopicsFacade;
import mt.edu.um.topic.TopicsFacadeImpl;
import mt.edu.um.topictree.TopicTreeFacade;
import mt.edu.um.topictree.TopicTreeFacadeImpl;
import mt.edu.um.topictree.TopicTreeImpl;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by matthew on 02/01/2016.
 */
public class EventHandler {

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final TopicTreeFacade topicTreeFacade = new TopicTreeFacadeImpl(new TopicTreeImpl());
    private final SubscribersFacade subscribersFacade = new SubscribersFacadeImpl();
    private final TopicsFacade topicsFacade = new TopicsFacadeImpl();
    private final SubscriberConnections subscriberConnections = new SubscriberConnections();

    public EventHandler() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            List<Subscriber> timedOutSubscribers = subscribersFacade.getTimedOutConnections(5000L);
            timedOutSubscribers.forEach(subscriber -> closeConnection(subscriberConnections.get(subscriber.getId())));
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void handleMessages(Connection origin) {
        List<Message> messages = origin.getIncomingMessages().emptyBuffer();
        messages.stream().forEach(message -> CompletableFuture.runAsync(() -> message.accept(new MessageHandler(topicTreeFacade, subscribersFacade, topicsFacade, subscriberConnections, origin)), executorService));
    }

    public void closeConnection(Connection connection) {
        CompletableFuture.runAsync(() -> {
            subscribersFacade.unsubscribe(connection.getSubscriberId());
            connection.getOutgoingMessages().emptyBuffer();
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
