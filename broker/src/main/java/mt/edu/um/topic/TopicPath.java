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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Topic topic : topics) {
            builder.append(topic.toString());
        }
        return builder.toString();
    }
}
