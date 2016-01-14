package mt.edu.um.monitor;

import javax.management.MXBean;
import java.time.Instant;

/**
 * Created by matthew on 14/01/2016.
 */
@MXBean
public interface ConnectionMonitor {

    int getClientId();

    int getReceivedMessages();

    int getSentMessages();

    Instant getLastActiveTime();

}
