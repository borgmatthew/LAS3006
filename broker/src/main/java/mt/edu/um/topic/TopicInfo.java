package mt.edu.um.topic;

/**
 * Created by matthew on 16/01/2016.
 */
public class TopicInfo {

    private int publishedMessages;
    private int subscribers;
    private final TopicPath topicPath;

    public TopicInfo(TopicPath topicPath) {
        publishedMessages = 0;
        subscribers = 0;
        this.topicPath = topicPath;
    }

    public int getPublishedMessages() {
        return publishedMessages;
    }

    public TopicInfo setPublishedMessages(int publishedMessages) {
        this.publishedMessages = publishedMessages;
        return this;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public TopicInfo setSubscribers(int subscribers) {
        this.subscribers = subscribers;
        return this;
    }

    public TopicPath getTopicPath() {
        return topicPath;
    }
}
