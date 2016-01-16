package mt.edu.um.monitor;

import javax.management.MXBean;

/**
 * Created by matthew on 16/01/2016.
 */
@MXBean
public interface TopicMonitor {

    int getPublishedMessages();

    int getNumberOfSubscribers();

    String getTopic();
}
