package mt.edu.um.core;

import mt.edu.um.topic.TopicPath;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by matthew on 09/01/2016.
 */
public class SubscriberTopics {

    final Map<Integer, List<TopicPath>> subscriberTopics;
    final ReadWriteLock readWriteLock;

    public SubscriberTopics() {
        subscriberTopics = new HashMap<>();
        readWriteLock = new ReentrantReadWriteLock();
    }

    public void addTopic(int subscriberId, TopicPath topicPath) {
        try {
            readWriteLock.writeLock().lock();
            if(subscriberTopics.containsKey(subscriberId)) {
                subscriberTopics.get(subscriberId).add(topicPath);
            } else {
                subscriberTopics.put(subscriberId, new ArrayList<>(Arrays.asList(topicPath)));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public List<TopicPath> getTopics(int subscriberId) {
        try {
            readWriteLock.readLock().lock();
            return subscriberTopics.getOrDefault(subscriberId, Collections.emptyList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void removeTopic (int subscriberId, TopicPath topicPath) {
        try {
            readWriteLock.writeLock().lock();
            if(subscriberTopics.containsKey(subscriberId)) {
                List topicList = subscriberTopics.get(subscriberId);
                topicList.remove(topicPath);
                if(topicList.size() == 0) {
                    subscriberTopics.remove(subscriberId);
                }
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
