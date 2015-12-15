package mt.edu.um.task;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;
import mt.edu.um.topictree.TopicTreeFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class PublishTask implements Callable<List<Subscriber>> {

    private TopicTreeFacade topicTreeFacade;
    private TopicPath topicPath;

    public PublishTask(TopicTreeFacade topicTreeFacade, TopicPath topicPath) {
        this.topicTreeFacade = topicTreeFacade;
        this.topicPath = topicPath;
    }

    @Override
    public List<Subscriber> call() throws Exception {
        return new ArrayList(topicTreeFacade.getSubscribers(topicPath));
    }
}
