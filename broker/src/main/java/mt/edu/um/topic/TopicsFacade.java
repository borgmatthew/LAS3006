package mt.edu.um.topic;

import java.util.List;

/**
 * Created by matthew on 02/12/2015.
 */
public interface TopicsFacade {

    TopicPath convertToTopicPath(String path);

    void register(TopicPath topicPath);

    void unregister(TopicPath topicPath);

    List<TopicPath> getAllTopics();

    TopicInfo getTopicInfo(TopicPath topicPath);

}
