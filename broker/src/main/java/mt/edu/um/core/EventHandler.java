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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by matthew on 02/01/2016.
 */
public class EventHandler {

    private final ExecutorService executorService;
    private final TopicTreeFacade topicTreeFacade = new TopicTreeFacadeImpl(new TopicTreeImpl());
    private final SubscribersFacade subscribersFacade = new SubscribersFacadeImpl();
    private final TopicsFacade topicsFacade = new TopicsFacadeImpl();
    private final ConnectionManager connectionManager;
    private final ConcurrentHashMap<Integer, Connection> connectionMapper = new ConcurrentHashMap<>();

    public EventHandler(ConnectionManager connectionManager) {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.connectionManager = connectionManager;
    }

    public void handleMessages(Connection origin) {
        List<Message> messages = origin.getIncomingMessages().emptyBuffer();
        connectionManager.update(origin);
        messages.stream()
                .forEach(message -> CompletableFuture.runAsync(() -> {
                    message.accept(new MessageHandler(topicTreeFacade, subscribersFacade, topicsFacade, connectionMapper, origin));
                }, executorService));
    }

    /**
     * When closing a connection, do the following actions:
     *  1. Get the subscriber represented by that connection (if any)
     *  2. Unsubscribe from all the topics
     *  3. Remove subscriber
     *  4. Remove connection mapping for that subscriber
     *  5. Clear all outgoing messages for that connection
     *  6. Remove connection from list of connections
     *  7. Close the socket
     * @param connection
     */
    public void closeConnection(Connection connection) {
        CompletableFuture.runAsync(() -> {
            System.out.println("Disconnecting client " + connection.getSubscriberId() + "\n");

            Optional<Subscriber> subscriberOptional = subscribersFacade.get(connection.getSubscriberId());
            if (subscriberOptional.isPresent()) {
                subscriberOptional.get().getTopics().stream().forEach(topic -> topicTreeFacade.unsubscribe(topic, subscriberOptional.get()));
                subscribersFacade.remove(subscriberOptional.get().getId());
                connectionMapper.remove(connection.getSubscriberId());
            }

            connection.getOutgoingMessages().emptyBuffer();

            try {
                connection.getSelectionKey().channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, executorService);

        connectionManager.removeConnection(connection);
    }

}
