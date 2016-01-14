package mt.edu.um.core;

import mt.edu.um.monitor.ConnectionMonitorImpl;
import mt.edu.um.monitor.ConnectionsMonitorImpl;
import mt.edu.um.protocol.communication.BrokerProtocol;
import mt.edu.um.protocol.communication.BrokerProtocolImpl;
import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by matthew on 10/12/2015.
 */
public class Server {

    private final EventHandler eventHandler;
    private final BrokerProtocol brokerProtocol = new BrokerProtocolImpl();
    private final int port;
    private final int maxInactiveMinutes;
    private final ConnectionManager connectionManager;
    private long nextConnectionExpiry;

    public Server(int port, int maxInactiveMinutes) {
        this.port = port;
        this.maxInactiveMinutes = maxInactiveMinutes;
        this.connectionManager = new ConnectionManager();
        this.eventHandler = new EventHandler(connectionManager);
        this.nextConnectionExpiry = maxInactiveMinutes * 60 * 1000;

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ConnectionsMonitorImpl connectionMonitor = new ConnectionsMonitorImpl(connectionManager);
        try {
            mBeanServer.registerMBean(connectionMonitor, connectionMonitor.getObjectName());
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try (Selector selector = Selector.open();
            ServerSocketChannel socketChannel = ServerSocketChannel.open()) {

            socketChannel.configureBlocking(false);
            socketChannel.bind(new InetSocketAddress(port));
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                if (selector.select(nextConnectionExpiry) > 0) {
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isValid() && key.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                            SocketChannel clientChannel = serverSocketChannel.accept();
                            clientChannel.configureBlocking(false);
                            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                            Connection connection = new Connection(clientKey);
                            connectionManager.addConnection(connection);
                            clientKey.attach(connection);
                            createConnectionMBean(connection);
                        }

                        if (key.isValid() && key.isReadable()) {
                            try {
                                Connection connection = (Connection) key.attachment();
                                connectionManager.update(connection);
                                connection.getIncomingMessages().addAll(brokerProtocol.receive((SocketChannel) key.channel()));
                                eventHandler.handleMessages(connection);
                            } catch (IOException e) {
                                key.cancel();
                            }
                        }

                        if (key.isValid() && key.isWritable()) {
                            List<Message> outgoingMessages = ((Connection)key.attachment()).getOutgoingMessages().emptyBuffer();
                            outgoingMessages.forEach(message -> {
                                try {
                                    brokerProtocol.send((SocketChannel) key.channel(), message);
                                } catch (IOException e) {
                                    key.cancel();
                                }
                            });
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if (!key.isValid()) {
                            eventHandler.closeConnection((Connection)key.attachment());
                        }

                    }
                }

                final long nowInMillis = Instant.now().toEpochMilli();
                connectionManager.getTimedOutConnections(nowInMillis - (maxInactiveMinutes * 60 * 1000L))
                        .stream()
                        .forEach(eventHandler::closeConnection);

                nextConnectionExpiry = Math.abs((connectionManager.getLastEntry().orElse(nowInMillis) + maxInactiveMinutes * 60) - nowInMillis);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConnectionMBean(Connection connection) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ConnectionMonitorImpl connectionMonitor = new ConnectionMonitorImpl(connection);
        try {
            mBeanServer.registerMBean(connectionMonitor, connectionMonitor.getObjectName());
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }
}
