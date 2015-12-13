package mt.edu.um.core;

import mt.edu.um.graph.Node;
import mt.edu.um.subscriber.Subscriber;
import mt.edu.um.topic.Topic;
import mt.edu.um.topic.TopicPath;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicTreeImpl implements TopicTree {

    private Node<Topic, Set<Subscriber>> root = new Node<>(new Topic(""), new HashSet<>(0));

    @Override
    public boolean insert(TopicPath key, Set<Subscriber> value) {
        if (key.getTopics().size() > 0 && value.size() > 0) {
            return recursiveInsert(key, value, 0, root);
        } else {
            return false;
        }
    }

    @Override
    public boolean contains(TopicPath key) {
        if (key.getTopics().size() > 0) {
            return recursiveContains(key, 0, root);
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(TopicPath key) {
        if (key.getTopics().size() > 0) {
            return recursiveRemove(key, 0, root);
        } else {
            return false;
        }
    }

    @Override
    public Set<Subscriber> get(TopicPath key) {
        if (key.getTopics().size() > 0) {
            return recursiveGet(key, 0, root);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Subscriber> getSubscribers(TopicPath key) {
        if (key.getTopics().size() > 0) {
            return recursiveGetSubscribers(key, 0, new HashSet<>(Arrays.asList(root)));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public void traverse(Consumer<Set<Subscriber>> consumer) {
        Stack<Node<Topic, Set<Subscriber>>> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            Node<Topic, Set<Subscriber>> root = stack.pop();
            Map<Topic, Node<Topic, Set<Subscriber>>> children = root.getChildren();
            for (Node<Topic, Set<Subscriber>> node : children.values()) {
                consumer.accept(node.getValue());
                //TODO: remove code to print tree
                System.out.println(root.getKey().getName() + " -> " + node.getKey().getName() + ";");
                stack.push(node);
            }
        }
    }

    @Override
    public List<Object> traverse(Function<Set<Subscriber>, Object> function) {
        List<Object> result = new ArrayList<>();
        Stack<Node<Topic, Set<Subscriber>>> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            Node<Topic, Set<Subscriber>> root = stack.pop();
            Map<Topic, Node<Topic, Set<Subscriber>>> children = root.getChildren();
            for (Node<Topic, Set<Subscriber>> node : children.values()) {
                result.add(function.apply(node.getValue()));
                stack.push(node);
            }
        }
        return result;
    }

    @Override
    public int size() {
        List<Object> sizes = traverse(subscribers -> 1);
        return sizes.size();
    }

    private boolean recursiveInsert(TopicPath key, Set<Subscriber> value, int depth, Node<Topic, Set<Subscriber>> node) {
        if (depth == key.getTopics().size() - 1) {
            Topic topic = key.getTopics().get(depth);
            if (!node.getChildren().keySet().contains(topic)) {
                node.getChildren().put(topic, new Node<>(topic, value));
                return true;
            }
            return false;
        } else {
            Topic topic = key.getTopics().get(depth);
            Node<Topic, Set<Subscriber>> nextNode;
            if (!node.getChildren().keySet().contains(topic)) {
                nextNode = new Node<>(topic, new HashSet<>());
                node.getChildren().put(topic, nextNode);
            } else {
                nextNode = node.getChildren().get(topic);
            }
            return recursiveInsert(key, value, 1 + depth, nextNode);
        }
    }

    private Set<Subscriber> recursiveGet(TopicPath key, int depth, Node<Topic, Set<Subscriber>> parent) {
        if (depth == key.getTopics().size()-1) {
            Topic topic = key.getTopics().get(depth);
            if(parent.getChildren().containsKey(topic)) {
                return parent.getChildren().get(topic).getValue();
            } else {
                return Collections.emptySet();
            }
        } else {
            Topic topic = key.getTopics().get(depth);
            if(parent.getChildren().containsKey(topic)) {
                return recursiveGet(key, depth + 1, parent.getChildren().get(topic));
            } else {
                return Collections.emptySet();
            }
        }
    }

    private boolean recursiveContains(TopicPath key, int depth, Node<Topic, Set<Subscriber>> parent) {
        if (depth == key.getTopics().size()-1) {
            if(parent.getChildren().containsKey(key.getTopics().get(depth))) {
                return true;
            } else {
                return false;
            }
        } else {
            Topic topic = key.getTopics().get(depth);
            if(parent.getChildren().containsKey(topic)) {
                return recursiveContains(key, depth + 1, parent.getChildren().get(topic));
            } else {
                return false;
            }
        }
    }

    private Set<Subscriber> recursiveGetSubscribers(TopicPath key, int depth, Set<Node<Topic, Set<Subscriber>>> matchingNodes) {
        if (matchingNodes.size() == 0) {
            return Collections.emptySet();
        } else if (depth == key.getTopics().size()) {
            Set<Subscriber> subscribers = new HashSet<>();
            for (Node<Topic, Set<Subscriber>> node : matchingNodes) {
                subscribers.addAll(node.getValue());
            }
            return subscribers;
        } else {
            Set<Node<Topic, Set<Subscriber>>> result = new HashSet<>();
            Set<Subscriber> subscribers = new HashSet<>();
            for (Node<Topic, Set<Subscriber>> node : matchingNodes) {
                if (node.getChildren().containsKey(new Topic("#"))) {
                    subscribers.addAll(node.getChildren().get(new Topic("#")).getValue());
                }
                result.addAll(matches(node, key.getTopics().get(depth)));
            }
            subscribers.addAll(recursiveGetSubscribers(key, depth + 1, result));
            return subscribers;
        }
    }

    private Set<Node<Topic, Set<Subscriber>>> matches(Node<Topic, Set<Subscriber>> node, Topic topic) {
        Set<Node<Topic, Set<Subscriber>>> matchedNodes = new HashSet<>();
        if (node.getChildren().containsKey(topic)) {
            matchedNodes.add(node.getChildren().get(topic));
        }

        if (node.getChildren().containsKey(new Topic("+"))) {
            matchedNodes.add(node.getChildren().get(new Topic("+")));
        }

        return matchedNodes;
    }

    private boolean recursiveRemove(TopicPath key, int depth, Node<Topic, Set<Subscriber>> node) {
        if (depth == key.getTopics().size() - 1) {
            if (node.getChildren().containsKey(key.getTopics().get(depth))) {
                Map<Topic, Node<Topic, Set<Subscriber>>> grandChildren = node.getChildren().get(key.getTopics().get(depth)).getChildren();
                if (grandChildren.isEmpty()) {
                    node.getChildren().remove(key.getTopics().get(depth));
                }
                return true;
            } else {
                return false;
            }
        } else {
            if (node.getChildren().containsKey(key.getTopics().get(depth))) {
                boolean result = recursiveRemove(key, depth + 1, node.getChildren().get(key.getTopics().get(depth)));
                Node<Topic, Set<Subscriber>> childNode = node.getChildren().get(key.getTopics().get(depth));
                if (childNode.getChildren().isEmpty() && childNode.getValue().isEmpty()) {
                    node.getChildren().remove(key.getTopics().get(depth));
                }
                return result;
            } else {
                return false;
            }
        }
    }

}
