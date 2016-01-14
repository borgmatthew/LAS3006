package mt.edu.um.client;

import mt.edu.um.topic.TopicPath;

import java.util.List;
import java.util.Vector;

/**
 * Created by matthew on 26/11/2015.
 */
public class Client {

    private int id;
    private List<TopicPath> topics = new Vector<>();

    public Client(int id) {
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

        Client that = (Client) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
