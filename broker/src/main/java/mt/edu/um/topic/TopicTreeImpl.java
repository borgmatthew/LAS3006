package mt.edu.um.topic;

import mt.edu.um.graph.Node;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicTreeImpl implements TopicTree{

    private Node<Topic, Set<Subscriber>> root = new Node(new Topic(""), new HashSet<Subscriber>(0));

    @Override
    public boolean insert(TopicPath key, Set<Subscriber> value) {
        if(key.getTopics().size() > 0 && value.size() > 0) {
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
        if(key.getTopics().size() > 0) {
            return recursiveGet(key, 0, new HashSet<>(Arrays.asList(root)));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public void traverse(Consumer<Set<Subscriber>> consumer) {

    }

    private boolean recursiveInsert(TopicPath key, Set<Subscriber> value, int depth, Node<Topic, Set<Subscriber>> node) {
        if(depth == key.getTopics().size()-1) {
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

    private Set<Subscriber> recursiveGet(TopicPath key, int depth, Set<Node<Topic, Set<Subscriber>>> matchingNodes) {
        if(matchingNodes.size() == 0) {
            return Collections.emptySet();
        } else if(depth == key.getTopics().size()) {
            Set<Subscriber> subscribers = new HashSet<>();
            for(Node<Topic, Set<Subscriber>> node : matchingNodes) {
                subscribers.addAll(node.getValue());
            }
            return subscribers;
        } else {
            Set<Node<Topic, Set<Subscriber>>> result = new HashSet<>();
            Set<Subscriber> subscribers = new HashSet<>();
            for (Node<Topic, Set<Subscriber>> node : matchingNodes) {
                if(node.getChildren().containsKey(new Topic("#"))) {
                    subscribers.addAll(node.getChildren().get(new Topic("#")).getValue());
                }
                result.addAll(matches(node, key.getTopics().get(depth)));
            }
            subscribers.addAll(recursiveGet(key, depth + 1, result));
            return subscribers;
        }
    }

    private Set<Node<Topic, Set<Subscriber>>> matches(Node<Topic, Set<Subscriber>> node, Topic topic) {
        Set<Node<Topic, Set<Subscriber>>> matchedNodes = new HashSet<>();
        if(node.getChildren().containsKey(topic)) {
            matchedNodes.add(node.getChildren().get(topic));
        }

        if(node.getChildren().containsKey(new Topic("+"))) {
            matchedNodes.add(node.getChildren().get(new Topic("+")));
        }

        if(node.getChildren().containsKey(new Topic("#"))) {
            matchedNodes.add(node.getChildren().get(new Topic("#")));
        }

        return matchedNodes;
    }


}
