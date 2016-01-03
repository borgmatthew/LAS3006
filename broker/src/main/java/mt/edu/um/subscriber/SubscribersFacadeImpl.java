package mt.edu.um.subscriber;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by matthew on 13/12/2015.
 */
public class SubscribersFacadeImpl implements SubscribersFacade {

    private ReentrantReadWriteLock lock;
    Map<Integer, Subscriber> subscribers = new HashMap<>();

    public SubscribersFacadeImpl() {
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean subscribe(int id) {
        try {
            lock.writeLock().lock();
            if (subscribers.containsKey(id)) {
                return false;
            }

            subscribers.put(id, new Subscriber(id, LocalDateTime.now()));
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean unsubscribe(int id) {
        try {
            lock.writeLock().lock();
            if (!subscribers.containsKey(id)) {
                return false;
            }

            subscribers.remove(id);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean update(int id, LocalDateTime lastActive) {
        try {
            lock.writeLock().lock();
            if (!subscribers.containsKey(id)) {
                return false;
            }

            subscribers.get(id).setLastActivityTime(lastActive);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Subscriber> get(int id) {
        try {
            lock.readLock().lock();
            if (!subscribers.containsKey(id)) {
                return Optional.empty();
            }

            return Optional.of(subscribers.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Subscriber> getTimedOutConnections(long seconds) {
        try {
            lock.writeLock().lock();
            LocalDateTime now = LocalDateTime.now();

            return subscribers.entrySet().stream()
                    .filter(entry -> now.minusSeconds(seconds).isAfter(entry.getValue().getLastActivityTime()))
                    .map(entry -> entry.getValue())
                    .collect(Collectors.toList());

        } finally {
            lock.writeLock().unlock();
        }
    }
}
