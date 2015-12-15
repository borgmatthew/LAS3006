package mt.edu.um.core;

import mt.edu.um.topictree.TopicTreeFacade;
import mt.edu.um.topictree.TopicTreeFacadeImpl;
import mt.edu.um.topictree.TopicTreeImpl;

/**
 * Created by matthew on 10/12/2015.
 */
public class Server {

    private TopicTreeFacade topicTreeFacade = new TopicTreeFacadeImpl(new TopicTreeImpl());

    public void start() {

    }
}
