package mt.edu.um.subscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by matthew on 13/12/2015.
 */
public class SubscribersFacadeImpl implements SubscribersFacade {

    private ReentrantReadWriteLock lock;
    private final Map<Integer, Subscriber> subscribers = new HashMap<>();

    public SubscribersFacadeImpl() {
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean create(int id) {
        try {
            lock.writeLock().lock();
            if (subscribers.containsKey(id)) {
                return false;
            }

            subscribers.put(id, new Subscriber(id));
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(int id) {
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
}
