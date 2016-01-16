package mt.edu.um.monitor;

import javax.management.MXBean;
import java.util.List;

/**
 * Created by matthew on 16/01/2016.
 */
@MXBean
public interface TopicsMonitor {

    int getTotalTopics();

    List<String> getAllTopics();

}
