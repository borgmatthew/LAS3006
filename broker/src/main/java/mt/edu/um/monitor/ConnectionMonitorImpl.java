package mt.edu.um.monitor;

import mt.edu.um.protocol.connection.Connection;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.time.Instant;

/**
 * Created by matthew on 14/01/2016.
 */
public class ConnectionMonitorImpl implements ConnectionMonitor {

    private final Connection connection;

    public ConnectionMonitorImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int getClientId() {
        return connection.getClientId();
    }

    @Override
    public int getReceivedMessages() {
        return connection.getIncomingMessages().getTotalMessagesBuffered();
    }

    @Override
    public int getSentMessages() {
        return connection.getOutgoingMessages().getTotalMessagesBuffered();
    }

    @Override
    public Instant getLastActiveTime() {
        return connection.getLastActive();
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("mt.edu.um.core:type=Connections,category=connection,name=connection#" + connection.getObjectCreationTime().toEpochMilli());
    }
}
