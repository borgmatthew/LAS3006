package mt.edu.um.core;

import mt.edu.um.protocol.connection.Connection;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by matthew on 12/01/2016.
 */
public class ConnectionManager {

    private final SortedMap<CompositeKeyEntry, Connection> connectionList = new TreeMap<>((o1, o2) -> {
        int timeCompare = o1.getLastActiveTime().compareTo(o2.getLastActiveTime());
        if(timeCompare != 0) {
            return timeCompare;
        } else {
            return o1.getCreationTime().compareTo(o2.getCreationTime());
        }
    });

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void addConnection(Connection connection) {
        lock.writeLock().lock();
        try {
            connectionList.put(new CompositeKeyEntry(connection.getLastActive(), connection.getObjectCreationTime()), connection);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeConnection(Connection connection) {
        lock.writeLock().lock();
        try {
            connectionList.remove(new CompositeKeyEntry(connection.getLastActive(), connection.getObjectCreationTime()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void update(Connection connection) {
        lock.writeLock().lock();
        try {
            connectionList.remove(new CompositeKeyEntry(connection.getLastActive(), connection.getObjectCreationTime()));
            Instant lastActiveTime = Instant.now();
            connection.setLastActive(lastActiveTime);
            connectionList.put(new CompositeKeyEntry(lastActiveTime, connection.getObjectCreationTime()), connection);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Long> getLastEntry() {
        try {
            lock.readLock().lock();
            if (!connectionList.isEmpty()) {
                return Optional.of(connectionList.firstKey().getLastActiveTime().toEpochMilli());
            } else {
                return Optional.empty();
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public Collection<Connection> getTimedOutConnections(long timeoutInMillis) {
        lock.readLock().lock();
        try {
            return connectionList.headMap(new CompositeKeyEntry(Instant.ofEpochMilli(timeoutInMillis), Instant.ofEpochMilli(timeoutInMillis))).values();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getTotalConnections() {
        lock.readLock().lock();
        try {
            return connectionList.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    private class CompositeKeyEntry {

        private Instant lastActiveTime;
        private Instant creationTime;

        public CompositeKeyEntry(Instant instant, Instant creationTime) {
            this.lastActiveTime = instant;
            this.creationTime = creationTime;
        }

        public Instant getLastActiveTime() {
            return lastActiveTime;
        }

        public Instant getCreationTime() {
            return creationTime;
        }
    }
}
