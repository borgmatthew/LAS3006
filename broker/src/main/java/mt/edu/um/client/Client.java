package mt.edu.um.client;

import mt.edu.um.topic.TopicPath;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by matthew on 26/11/2015.
 */
public class Client {

    private int id;
    private List<TopicPath> topics = new Vector<>();
    private AtomicInteger publishedMessages;
    private AtomicInteger receivedMessages;

    public Client(int id) {
        this.id = id;
        this.publishedMessages = new AtomicInteger(0);
        this.receivedMessages = new AtomicInteger(0);
    }

    public int getId() {
        return id;
    }

    public List<TopicPath> getTopics() {
        return topics;
    }

    public AtomicInteger getReceivedMessages() {
        return receivedMessages;
    }

    public AtomicInteger getPublishedMessages() {
        return publishedMessages;
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
