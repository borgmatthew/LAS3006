package mt.edu.um.topictree;

import mt.edu.um.graph.Node;
import mt.edu.um.client.Client;
import mt.edu.um.topic.Topic;
import mt.edu.um.topic.TopicPath;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicTreeImpl implements TopicTree {

    private Node<Topic, Set<Client>> root = new Node<>(new Topic(""), new HashSet<>(0));

    @Override
    public boolean insert(TopicPath key, Set<Client> value) {
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
    public Set<Client> get(TopicPath key) {
        if (key.getTopics().size() > 0) {
            return recursiveGet(key, 0, root);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Set<Client> getSubscribers(TopicPath key) {
        if (key.getTopics().size() > 0) {
            return recursiveGetSubscribers(key, 0, new HashSet<>(Arrays.asList(root)));
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public void traverse(Consumer<Set<Client>> consumer) {
        Stack<Node<Topic, Set<Client>>> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            Node<Topic, Set<Client>> root = stack.pop();
            Map<Topic, Node<Topic, Set<Client>>> children = root.getChildren();
            for (Node<Topic, Set<Client>> node : children.values()) {
                consumer.accept(node.getValue());
                //TODO: remove code to print tree
                System.out.println(root.getKey().getName() + " -> " + node.getKey().getName() + ";");
                stack.push(node);
            }
        }
    }

    @Override
    public <K> List<K> traverse(Function<Set<Client>, K> function) {
        List<K> result = new ArrayList<>();
        Stack<Node<Topic, Set<Client>>> stack = new Stack<>();
        stack.push(root);
        while (!stack.empty()) {
            Node<Topic, Set<Client>> root = stack.pop();
            Map<Topic, Node<Topic, Set<Client>>> children = root.getChildren();
            for (Node<Topic, Set<Client>> node : children.values()) {
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

    private boolean recursiveInsert(TopicPath key, Set<Client> value, int depth, Node<Topic, Set<Client>> node) {
        if (depth == key.getTopics().size() - 1) {
            Topic topic = key.getTopics().get(depth);
            if (!node.getChildren().keySet().contains(topic)) {
                node.getChildren().put(topic, new Node<>(topic, value));
            } else {
                node.getChildren().get(topic).getValue().addAll(value);
            }
            return true;
        } else {
            Topic topic = key.getTopics().get(depth);
            Node<Topic, Set<Client>> nextNode;
            if (!node.getChildren().keySet().contains(topic)) {
                nextNode = new Node<>(topic, new HashSet<>());
                node.getChildren().put(topic, nextNode);
            } else {
                nextNode = node.getChildren().get(topic);
            }
            return recursiveInsert(key, value, 1 + depth, nextNode);
        }
    }

    private Set<Client> recursiveGet(TopicPath key, int depth, Node<Topic, Set<Client>> parent) {
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

    private boolean recursiveContains(TopicPath key, int depth, Node<Topic, Set<Client>> parent) {
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

    private Set<Client> recursiveGetSubscribers(TopicPath key, int depth, Set<Node<Topic, Set<Client>>> matchingNodes) {
        if (matchingNodes.size() == 0) {
            return Collections.emptySet();
        } else if (depth == key.getTopics().size()) {
            Set<Client> clients = new HashSet<>();
            for (Node<Topic, Set<Client>> node : matchingNodes) {
                clients.addAll(node.getValue());
            }
            return clients;
        } else {
            Set<Node<Topic, Set<Client>>> result = new HashSet<>();
            Set<Client> clients = new HashSet<>();
            for (Node<Topic, Set<Client>> node : matchingNodes) {
                if (node.getChildren().containsKey(new Topic("#"))) {
                    clients.addAll(node.getChildren().get(new Topic("#")).getValue());
                }
                result.addAll(matches(node, key.getTopics().get(depth)));
            }
            clients.addAll(recursiveGetSubscribers(key, depth + 1, result));
            return clients;
        }
    }

    private Set<Node<Topic, Set<Client>>> matches(Node<Topic, Set<Client>> node, Topic topic) {
        Set<Node<Topic, Set<Client>>> matchedNodes = new HashSet<>();
        if (node.getChildren().containsKey(topic)) {
            matchedNodes.add(node.getChildren().get(topic));
        }

        if (node.getChildren().containsKey(new Topic("+"))) {
            matchedNodes.add(node.getChildren().get(new Topic("+")));
        }

        return matchedNodes;
    }

    private boolean recursiveRemove(TopicPath key, int depth, Node<Topic, Set<Client>> node) {
        if (depth == key.getTopics().size() - 1) {
            if (node.getChildren().containsKey(key.getTopics().get(depth))) {
                Map<Topic, Node<Topic, Set<Client>>> grandChildren = node.getChildren().get(key.getTopics().get(depth)).getChildren();
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
                Node<Topic, Set<Client>> childNode = node.getChildren().get(key.getTopics().get(depth));
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
