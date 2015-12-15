package mt.edu.um.task;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;
import mt.edu.um.topictree.TopicTreeFacade;

import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class UnsubscribeTask implements Callable<Boolean> {

    private TopicTreeFacade topicTreeFacade;
    private TopicPath topicPath;
    private Subscriber subscriber;

    public UnsubscribeTask(TopicTreeFacade topicTreeFacade, TopicPath topicPath, Subscriber subscriber) {
        this.topicTreeFacade = topicTreeFacade;
        this.topicPath = topicPath;
        this.subscriber = subscriber;
    }

    @Override
    public Boolean call() throws Exception {
        return topicTreeFacade.unsubscribe(topicPath, subscriber);
    }
}
