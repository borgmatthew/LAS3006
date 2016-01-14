package mt.edu.um.topictree;

import mt.edu.um.graph.Tree;
import mt.edu.um.client.Client;
import mt.edu.um.topic.TopicPath;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by matthew on 02/12/2015.
 */
public interface TopicTree extends Tree<TopicPath, Set<Client>> {

    List traverse(Function<Set<Client>, Object> function);

    Set<Client> getSubscribers(TopicPath key);
}
