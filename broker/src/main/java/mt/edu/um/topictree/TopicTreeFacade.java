package mt.edu.um.topictree;

import mt.edu.um.client.Client;
import mt.edu.um.topic.TopicPath;

import java.util.List;
import java.util.Set;

/**
 * Created by matthew on 15/12/2015.
 */
public interface TopicTreeFacade {

    boolean subscribe(TopicPath topicPath, Set<Client> clients);

    boolean unsubscribe(TopicPath topicPath, Client client);

    Set<Client> getSubscribers(TopicPath topicPath);

    List<TopicPath> getAllTopics();
}
