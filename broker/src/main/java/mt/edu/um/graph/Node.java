package mt.edu.um.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthew on 02/12/2015.
 */
public class Node<K, V> {

    private K key;
    private Map<K, Node> children;
    private V value;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        children = new HashMap<>();
    }

    public Map<K, Node> getChildren() {
        return children;
    }

    public V getValue() {
        return value;
    }

    public K getKey() {
        return key;
    }
}
