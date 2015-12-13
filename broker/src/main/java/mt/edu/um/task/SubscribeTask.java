package mt.edu.um.task;

import mt.edu.um.core.TopicTree;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class SubscribeTask implements Callable<Boolean> {

    private TopicTree topicTree;
    private TopicPath topicPath;
    private Set<Subscriber> subscriberSet;

    public SubscribeTask(TopicTree topicTree, TopicPath topicPath) {
        this.topicTree = topicTree;
        this.topicPath = topicPath;
    }

    @Override
    public Boolean call() throws Exception {
        return topicTree.insert(topicPath, subscriberSet);
    }
}
