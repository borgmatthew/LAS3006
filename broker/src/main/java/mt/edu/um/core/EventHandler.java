package mt.edu.um.core;

import mt.edu.um.protocol.connection.Connection;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by matthew on 02/01/2016.
 */
public class EventHandler {

    private final ExecutorService executorService;
    private final TopicTreeFacade topicTreeFacade = new TopicTreeFacadeImpl(new TopicTreeImpl());
    private final SubscribersFacade subscribersFacade = new SubscribersFacadeImpl();
    private final TopicsFacade topicsFacade = new TopicsFacadeImpl();
    private final ConnectionManager connectionManager;
    private final SubscriberTopics subscriberTopics = new SubscriberTopics();

    public EventHandler(ConnectionManager connectionManager) {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.connectionManager = connectionManager;
    }

    public void handleMessages(Connection origin) {
        List<Message> messages = origin.getIncomingMessages().emptyBuffer();
        messages.stream()
                .forEach(message -> CompletableFuture.runAsync(() -> {
                    message.accept(new MessageHandler(topicTreeFacade, subscribersFacade, topicsFacade, connectionManager, subscriberTopics, origin));
                    subscribersFacade.update(origin.getSubscriberId(), LocalDateTime.now());
                }, executorService));
    }

    public void closeConnection(Connection connection) {
        CompletableFuture.runAsync(() -> {
            System.out.println("Disconnecting client " + connection.getSubscriberId() + "\n");
            Optional<Subscriber> subscriberOptional = subscribersFacade.get(connection.getSubscriberId());
            if(subscriberOptional.isPresent()) {
                connection.getOutgoingMessages().emptyBuffer();
                subscriberTopics.getTopics(subscriberOptional.get().getId()).forEach(topic -> topicTreeFacade.unsubscribe(topic, subscriberOptional.get()));
                subscribersFacade.unsubscribe(subscriberOptional.get().getId());
            }
            connectionManager.removeConnection(connection);
            try {
                connection.getSelectionKey().channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, executorService);
    }

}
