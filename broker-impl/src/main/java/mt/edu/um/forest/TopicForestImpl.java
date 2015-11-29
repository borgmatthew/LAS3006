package mt.edu.um.forest;

import mt.edu.um.broker.Topic;
import mt.edu.um.broker.TopicPath;
import mt.edu.um.broker.TopicSubscribers;

/**
 * Created by matthew on 29/11/2015.
 */
public class TopicForestImpl implements TopicForest {

    private Node<Topic, TopicSubscribers> root;

    private TopicForestImpl(){
        root = null;
    }

    @Override
    public boolean insert(TopicPath topics, TopicSubscribers subscribers) {
        return false;
    }

    @Override
    public boolean remove(TopicPath topics) {
        return false;
    }

    @Override
    public TopicSubscribers get(TopicPath topics) {
        return null;
    }
}
