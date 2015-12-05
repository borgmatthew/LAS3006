package mt.edu.um.graph;

import java.util.function.Consumer;

/**
 * Created by matthew on 02/12/2015.
 */
public interface Tree<K, V> {

    /**
     * Inserts a new entry in the tree
     * @param key a unique key
     * @param value the object to store
     * @return true on success, false on failure
     */
    boolean insert(K key, V value);

    /**
     * Checks if there is a value for the given key in the tree
     * @param key a unique key to search with
     * @return true if the value is found, false otherwise
     */
    boolean contains(K key);

    /**
     * Removes an entry from the tree
     * @param key a unique key
     * @return true on success, false on failure
     */
    boolean remove(K key);

    /**
     * Gets a value for this unique key
     * @param key a unique key
     * @return the value stored for that key
     */
    V get(K key);

    /**
     * Traverses the tree and applies a function to every node
     * @param consumer the function to apply
     */
    void traverse(Consumer<V> consumer);

    /**
     * Counts the number of nodes in the tree
     * @return the number of nodes
     */
    int size();
}
