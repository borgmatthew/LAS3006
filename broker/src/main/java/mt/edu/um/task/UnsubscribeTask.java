package mt.edu.um.task;

import mt.edu.um.core.TopicTree;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class UnsubscribeTask implements Callable<Boolean> {

    private TopicTree topicTree;
    private TopicPath topicPath;
    private Subscriber subscriber;

    public UnsubscribeTask(TopicTree topicTree, TopicPath topicPath, Subscriber subscriber) {
        this.topicTree = topicTree;
        this.topicPath = topicPath;
        this.subscriber = subscriber;
    }

    @Override
    public Boolean call() throws Exception {
        Set<Subscriber> subscribers = topicTree.get(topicPath);
        boolean result = false;

        if(subscribers.contains(subscriber)) {
            result = subscribers.remove(subscriber);
        }

        //Remove node if there are no more subscribers
        if(subscribers.size() == 0) {
            topicTree.remove(topicPath);
        }

        return result;
    }
}
