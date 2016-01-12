package mt.edu.um.core;

import mt.edu.um.protocol.connection.Connection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by matthew on 12/01/2016.
 */
public class ConnectionManager {
    private final ConcurrentHashMap<Integer, Connection> subscriberConnections = new ConcurrentHashMap<>();
    private final SortedMap<Long, Connection> connectionList = Collections.synchronizedSortedMap(new TreeMap<>((o1, o2) -> Math.negateExact(o1.compareTo(o2))));

    public ConnectionManager() {
    }

    public void addConnection(Connection connection) {
        connectionList.put(connection.getLastActive().getEpochSecond(), connection);
    }

    public void removeConnection(Connection connection) {
        if (subscriberConnections.contains(connection.getSubscriberId())) {
            subscriberConnections.remove(connection.getSubscriberId());
        }
        connectionList.remove(connection.getLastActive().getEpochSecond());
    }

    public void assign(int subscriberId, Connection connection) {
        subscriberConnections.put(subscriberId, connection);
    }

    public Connection getConnection(int subscriberId) {
        return subscriberConnections.get(subscriberId);
    }

    public Optional<Long> getLastEntry() {
        if(!connectionList.isEmpty()) {
            return Optional.of(connectionList.lastKey());
        } else {
            return Optional.empty();
        }
    }

    public Collection<Connection> getTimedOutConnections(long timeout) {
        return connectionList.tailMap(timeout).values();
    }
}
