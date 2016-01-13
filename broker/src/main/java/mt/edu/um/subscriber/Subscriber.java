package mt.edu.um.subscriber;

import mt.edu.um.topic.TopicPath;

import java.util.List;
import java.util.Vector;

/**
 * Created by matthew on 26/11/2015.
 */
public class Subscriber {

    private int id;
    private List<TopicPath> topics = new Vector<>();

    public Subscriber(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<TopicPath> getTopics() {
        return topics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subscriber that = (Subscriber) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
