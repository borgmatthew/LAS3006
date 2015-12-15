package mt.edu.um.task;

import mt.edu.um.topictree.TopicTree;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class PublishTask implements Callable<List<Subscriber>> {

    private TopicTree topicTree;
    private TopicPath topicPath;

    public PublishTask(TopicTree topicTree, TopicPath topicPath) {
        this.topicTree = topicTree;
        this.topicPath = topicPath;
    }

    @Override
    public List<Subscriber> call() throws Exception {
        return new ArrayList(topicTree.getSubscribers(topicPath));
    }
}
