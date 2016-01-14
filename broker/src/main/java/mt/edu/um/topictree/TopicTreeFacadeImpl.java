package mt.edu.um.topictree;

import mt.edu.um.client.Client;
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
    public boolean subscribe(TopicPath topicPath, Set<Client> clients) {
        try {
            lock.readLock().lock();
            return topicTree.insert(topicPath, clients);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean unsubscribe(TopicPath topicPath, Client client) {
        try {
            lock.writeLock().lock();
            if (topicTree.contains(topicPath)) {
                Set<Client> clients = topicTree.get(topicPath);
                boolean result = clients.remove(client);
                if (clients.size() == 0) {
                    topicTree.remove(topicPath);
                }
                return result;
            } else {
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<Client> getSubscribers(TopicPath topicPath) {
        try {
            lock.readLock().lock();
            return topicTree.getSubscribers(topicPath);
        } finally {
            lock.readLock().unlock();
        }
    }
}
