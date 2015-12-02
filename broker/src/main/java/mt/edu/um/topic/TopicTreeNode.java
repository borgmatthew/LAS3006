package mt.edu.um.topic;

import java.util.HashMap;
import java.util.List;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicTreeNode {

    private Topic key;
    private HashMap<Topic, TopicTreeNode> children;
    private List<Subscriber> value;

}
