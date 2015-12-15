package mt.edu.um.topictree;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe facade which implements a multiple reads single write policy
 */
public class TopicTreeFacadeImpl implements TopicTreeFacade {

    private TopicTree topicTree;
    private ReentrantReadWriteLock lock;

    public TopicTreeFacadeImpl(TopicTree topicTree) {
        this.topicTree = topicTree;
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean subscribe(TopicPath topicPath, Set<Subscriber> subscribers) {
        try{
            lock.readLock().lock();
            return topicTree.insert(topicPath, subscribers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean unsubscribe(TopicPath topicPath, Subscriber subscriber) {
        try{
            lock.writeLock().lock();
            if(topicTree.contains(topicPath)) {
                return topicTree.get(topicPath).remove(subscriber);
            } else {
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<Subscriber> getSubscribers(TopicPath topicPath) {
        try{
            lock.readLock().lock();
            return topicTree.getSubscribers(topicPath);
        } finally {
            lock.readLock().unlock();
        }
    }
}
