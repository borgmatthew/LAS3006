package mt.edu.um.forest;

import mt.edu.um.broker.Topic;
import mt.edu.um.broker.TopicPath;
import mt.edu.um.broker.TopicSubscribers;

import java.util.HashMap;

/**
 * Created by matthew on 29/11/2015.
 */
public class TopicForestImpl implements TopicForest {

    private static final TopicForest instance = new TopicForestImpl();
    private HashMap<Topic, Node<Topic, TopicSubscribers>> rootTopics;

    private TopicForestImpl(){
        rootTopics = null;
    }

    public static TopicForest getInstance() {
        return instance;
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
