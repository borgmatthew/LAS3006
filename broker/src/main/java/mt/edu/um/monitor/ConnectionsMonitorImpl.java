package mt.edu.um.monitor;

import mt.edu.um.core.ConnectionManager;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by matthew on 14/01/2016.
 */
public class ConnectionsMonitorImpl implements ConnectionsMonitor {

    private final ConnectionManager connectionManager;

    public ConnectionsMonitorImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public int getTotalConnections() {
        return connectionManager.getConnections().size();
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("mt.edu.um.core:type=Connections,name=connectionsMonitor");
    }
}
