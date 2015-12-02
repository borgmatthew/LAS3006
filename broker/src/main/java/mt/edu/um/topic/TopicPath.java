package mt.edu.um.topic;

import java.util.List;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicPath {

    private List<Topic> topics;

    public TopicPath(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Topic> getTopics() {
        return topics;
    }
}
