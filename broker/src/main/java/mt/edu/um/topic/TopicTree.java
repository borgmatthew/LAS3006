package mt.edu.um.topic;

import mt.edu.um.graph.Tree;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by matthew on 02/12/2015.
 */
public interface TopicTree extends Tree<TopicPath, Set<Subscriber>> {

    List traverse(Function<Set<Subscriber>, Object> function);

    Set<Subscriber> getSubscribers(TopicPath key);
}
