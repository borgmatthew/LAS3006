package mt.edu.um.core;

import mt.edu.um.client.Client;
import mt.edu.um.client.ClientsFacade;
import mt.edu.um.client.ClientsFacadeImpl;
import mt.edu.um.monitor.ClientMonitorImpl;
import mt.edu.um.monitor.ConnectionMonitorImpl;
import mt.edu.um.monitor.TopicsMonitorImpl;
import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;
import mt.edu.um.topic.TopicsFacade;
import mt.edu.um.topic.TopicsFacadeImpl;
import mt.edu.um.topictree.TopicTreeFacade;
import mt.edu.um.topictree.TopicTreeFacadeImpl;
import mt.edu.um.topictree.TopicTreeImpl;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
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
    private final ClientsFacade clientsFacade = new ClientsFacadeImpl();
    private final TopicsFacade topicsFacade = new TopicsFacadeImpl();
    private final ConnectionManager connectionManager;
    private final ConcurrentHashMap<Integer, Connection> connectionMapper = new ConcurrentHashMap<>();

    public EventHandler(ConnectionManager connectionManager) {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.connectionManager = connectionManager;
        createTopicsMonitorMBean(topicsFacade);
    }

    public void handleMessages(Connection origin) {
        List<Message> messages = origin.getIncomingMessages().emptyBuffer();
        messages.stream()
                .forEach(message -> CompletableFuture.runAsync(() -> {
                    message.accept(new MessageHandler(topicTreeFacade, clientsFacade, topicsFacade, connectionMapper, origin));
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
            System.out.println("Disconnecting client " + connection.getClientId() + "\n");

            Optional<Client> clientOptional = clientsFacade.get(connection.getClientId());
            if (clientOptional.isPresent()) {
                clientOptional.get().getTopics().stream().forEach(topic -> topicTreeFacade.unsubscribe(topic, clientOptional.get()));
                clientsFacade.remove(clientOptional.get().getId());
                connectionMapper.remove(connection.getClientId());
                unregisterClient(clientOptional.get());
            }

            connection.getOutgoingMessages().emptyBuffer();

            try {
                connection.getSelectionKey().channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, executorService);

        connectionManager.removeConnection(connection);

        unregisterConnection(connection);
    }

    private void unregisterConnection(Connection connection) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.unregisterMBean(new ConnectionMonitorImpl(connection).getObjectName());
        } catch (InstanceNotFoundException | MBeanRegistrationException | MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }

    private void unregisterClient(Client client) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.unregisterMBean(new ClientMonitorImpl(client).getObjectName());
        } catch (InstanceNotFoundException | MBeanRegistrationException | MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }

    private void createTopicsMonitorMBean(TopicsFacade topicsFacade) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        TopicsMonitorImpl topicsMonitor = new TopicsMonitorImpl(topicsFacade);
        try {
            mBeanServer.registerMBean(topicsMonitor, topicsMonitor.getObjectName());
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

}
