package mt.edu.um.topic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by matthew on 02/12/2015.
 */
public class TopicsFacadeImpl implements TopicsFacade {
    @Override
    public TopicPath convertToTopicPath(String path) {
        String[] splitted = path.split("/");
        List<Topic> topics = Arrays.stream(splitted)
                .map(topic -> new Topic(topic))
                .collect(Collectors.toList());
        return new TopicPath(topics);
    }
}
