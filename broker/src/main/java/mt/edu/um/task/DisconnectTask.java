package mt.edu.um.task;

import mt.edu.um.core.TopicTree;

import java.util.concurrent.Callable;

/**
 * Created by matthew on 11/12/2015.
 */
public class DisconnectTask implements Callable<Void> {

    private TopicTree topicTree;

    public DisconnectTask(TopicTree topicTree) {
        this.topicTree = topicTree;
    }

    @Override
    public Void call() throws Exception {
        return null;
    }
}
