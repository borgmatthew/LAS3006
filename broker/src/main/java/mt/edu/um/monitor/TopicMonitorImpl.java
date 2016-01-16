package mt.edu.um.monitor;

import mt.edu.um.topic.TopicInfo;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by matthew on 16/01/2016.
 */
public class TopicMonitorImpl implements TopicMonitor {

    private TopicInfo topicInfo;

    public TopicMonitorImpl(TopicInfo topicInfo) {
        this.topicInfo = topicInfo;
    }

    @Override
    public int getPublishedMessages() {
        return topicInfo.getPublishedMessages();
    }

    @Override
    public int getNumberOfSubscribers() {
        return topicInfo.getSubscribers();
    }

    @Override
    public String getTopic() {
        return topicInfo.getTopicPath().toString();
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("mt.edu.um.topic:type=Topics,category=topic,name=topic#" + getTopic());
    }
}
