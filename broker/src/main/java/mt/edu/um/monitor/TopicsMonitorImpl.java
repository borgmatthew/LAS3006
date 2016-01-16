package mt.edu.um.monitor;

import mt.edu.um.topic.TopicInfo;
import mt.edu.um.topic.TopicsFacade;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by matthew on 16/01/2016.
 */
public class TopicsMonitorImpl implements TopicsMonitor {

    private TopicsFacade topicsFacade;

    public TopicsMonitorImpl(TopicsFacade topicsFacade) {
        this.topicsFacade = topicsFacade;
    }

    @Override
    public int getTotalTopics() {
        return topicsFacade.getAllTopics().size();
    }

    @Override
    public List<String> getAllTopics() {
        return topicsFacade.getAllTopics().stream().map(topicInfo -> topicInfo.getTopicPath().toString()).collect(Collectors.toList());
    }

    @Override
    public int getTotalPublishMessages() {
        return topicsFacade.getAllTopics().stream().mapToInt(TopicInfo::getPublishedMessages).sum();
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("mt.edu.um.topic:type=Topics,name=topics");
    }
}
