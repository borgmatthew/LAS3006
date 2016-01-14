package mt.edu.um.monitor;

import javax.management.MXBean;
import java.util.List;

/**
 * Created by matthew on 14/01/2016.
 */
@MXBean
public interface ClientMonitor {

    int getId();

    List<String> getSubscribedTopics();

}
