package mt.edu.um.core;

import mt.edu.um.protocol.connection.Connection;

import java.time.Instant;
import java.util.*;

/**
 * Created by matthew on 12/01/2016.
 */
public class ConnectionManager {
    private final SortedMap<Long, Connection> connectionList = new TreeMap<>((o1, o2) -> Math.negateExact(o1.compareTo(o2)));

    public void addConnection(Connection connection) {
        connectionList.put(connection.getLastActive().toEpochMilli(), connection);
    }

    public void removeConnection(Connection connection) {
        connectionList.remove(connection.getLastActive().toEpochMilli());
    }

    public void update(Connection connection) {
        connectionList.remove(connection.getLastActive().toEpochMilli());
        Instant lastActiveTime = Instant.now();
        connection.setLastActive(lastActiveTime);
        connectionList.put(lastActiveTime.toEpochMilli(), connection);
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

    public ArrayList<Connection> getConnections() {
        return new ArrayList<>(connectionList.values());
    }
}
