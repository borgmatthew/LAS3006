package mt.edu.um.topic;

import mt.edu.um.graph.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicTreeImpl implements TopicTree{

    private Node<Topic, Set<Subscriber>> root = new Node(new Topic(""), new HashSet<Subscriber>(0));

    @Override
    public boolean insert(TopicPath key, Set<Subscriber> value) {
        return false;
    }

    @Override
    public boolean remove(TopicPath key) {
        return false;
    }

    @Override
    public Set<Subscriber> get(TopicPath key) {
        return null;
    }

    @Override
    public void traverse(Consumer<Set<Subscriber>> consumer) {

    }
}
