package mt.edu.um.topic;

import mt.edu.um.monitor.TopicMonitorImpl;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicsFacadeImpl implements TopicsFacade {

    private final HashMap<TopicPath, TopicInfo> informationOnTopics = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public TopicPath convertToTopicPath(String path) {
        String[] splitted = path.split("/");
        List<Topic> topics = Arrays.stream(splitted)
                .map(Topic::new)
                .collect(Collectors.toList());
        return new TopicPath(topics);
    }

    @Override
    public void register(TopicPath topicPath) {
        try {
            lock.writeLock().lock();
            if (!informationOnTopics.containsKey(topicPath)) {
                TopicInfo topicInfo = new TopicInfo(topicPath);
                topicInfo.setSubscribers(1);
                informationOnTopics.put(topicPath, topicInfo);
                createTopicBean(topicInfo);
            } else {
                TopicInfo topicInfo = informationOnTopics.get(topicPath);
                topicInfo.setSubscribers(topicInfo.getSubscribers() + 1);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void unregister(TopicPath topicPath) {
        try {
            lock.writeLock().lock();
            if (informationOnTopics.containsKey(topicPath)) {
                TopicInfo topicInfo = informationOnTopics.get(topicPath);
                topicInfo.setSubscribers(topicInfo.getSubscribers() - 1);
                if (topicInfo.getSubscribers() == 0) {
                    informationOnTopics.remove(topicPath);
                    unregisterTopicBean(topicInfo);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<TopicPath> getAllTopics() {
        try {
            lock.readLock().lock();
            return new ArrayList<>(informationOnTopics.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public TopicInfo getTopicInfo(TopicPath topicPath) {
        try {
            lock.readLock().lock();
            return informationOnTopics.get(topicPath);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void createTopicBean(TopicInfo topicInfo) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        TopicMonitorImpl topicMonitor = new TopicMonitorImpl(topicInfo);
        try {
            mBeanServer.registerMBean(topicMonitor, topicMonitor.getObjectName());
        } catch (InstanceAlreadyExistsException | MBeanRegistrationException | MalformedObjectNameException | NotCompliantMBeanException e) {
            e.printStackTrace();
        }
    }

    private void unregisterTopicBean(TopicInfo topicInfo) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.unregisterMBean(new TopicMonitorImpl(topicInfo).getObjectName());
        } catch (InstanceNotFoundException | MBeanRegistrationException | MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }
}
