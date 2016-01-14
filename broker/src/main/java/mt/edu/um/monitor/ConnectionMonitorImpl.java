package mt.edu.um.monitor;

import mt.edu.um.core.ConnectionManager;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by matthew on 14/01/2016.
 */
public class ConnectionMonitorImpl implements ConnectionMonitor {

    private final ConnectionManager connectionManager;

    public ConnectionMonitorImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public int getTotalConnections() {
        return connectionManager.getTotalConnections();
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("mt.edu.um.core:type=Connections,name=connectionMonitor");
    }
}
