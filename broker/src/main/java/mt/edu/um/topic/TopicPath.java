package mt.edu.um.topic;

import java.util.List;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicPath {

    private final List<Topic> topics;

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
            builder.append("/").append(topic.toString());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicPath topicPath = (TopicPath) o;

        return this.toString().equals(topicPath.toString());

    }

    @Override
    public int hashCode() {
        return topics != null ? topics.hashCode() : 0;
    }
}
