package mt.edu.um.task;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;
import mt.edu.um.topictree.TopicTreeFacade;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class SubscribeTask implements Callable<Boolean> {

    private TopicTreeFacade topicTreeFacade;
    private TopicPath topicPath;
    private Set<Subscriber> subscriberSet;

    public SubscribeTask(TopicTreeFacade topicTreeFacade, TopicPath topicPath) {
        this.topicTreeFacade = topicTreeFacade;
        this.topicPath = topicPath;
    }

    @Override
    public Boolean call() throws Exception {
        return topicTreeFacade.subscribe(topicPath, subscriberSet);
    }
}
