package mt.edu.um.forest;

import mt.edu.um.broker.TopicPath;
import mt.edu.um.broker.TopicSubscribers;

/**
 * Created by matthew on 29/11/2015.
 */
public interface TopicForest {

    boolean insert(TopicPath topics, TopicSubscribers subscribers);

    boolean remove(TopicPath topics);

    TopicSubscribers get(TopicPath topics);
}
