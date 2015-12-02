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
        if(key.getTopics().size() > 0 && value != null) {
            return recursiveInsert(key, value, 0, root);
        } else {
            return false;
        }
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

    private boolean recursiveInsert(TopicPath key, Set<Subscriber> value, int depth, Node<Topic, Set<Subscriber>> node) {
        if(depth == key.getTopics().size()) {
            Topic topic = key.getTopics().get(depth);
            if (!node.getChildren().keySet().contains(topic)) {
                node.getChildren().put(topic, new Node(topic, value));
                return true;
            }
            return false;
        } else {
            Topic topic = key.getTopics().get(depth);
            Node<Topic, Set<Subscriber>> nextNode;
            if (!node.getChildren().keySet().contains(topic)) {
                nextNode = new Node(topic, new HashSet<Subscriber>());
                node.getChildren().put(topic, nextNode);
            } else {
                nextNode = node.getChildren().get(topic);
            }
            return recursiveInsert(key, value, 1+depth, nextNode);
        }
    }

    private Set<Subscriber> recursiveGet(){}
}
