package mt.edu.um.topictree;

import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.TopicPath;

import java.util.Set;

/**
 * Created by matthew on 15/12/2015.
 */
public interface TopicTreeFacade {

    boolean subscribe(TopicPath topicPath, Set<Subscriber> subscribers);

    boolean unsubscribe(TopicPath topicPath, Subscriber subscriber);

    Set<Subscriber> getSubscribers(TopicPath topicPath);
}
