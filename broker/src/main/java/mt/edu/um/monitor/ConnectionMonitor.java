package mt.edu.um.monitor;

import javax.management.MXBean;

/**
 * Created by matthew on 14/01/2016.
 */
@MXBean
public interface ConnectionMonitor {

    int getTotalConnections();

}
