package mt.edu.um.protocol.connection;

import java.nio.channels.SelectionKey;
import java.time.Instant;

/**
 * Created by matthew on 02/01/2016.
 */
public class Connection {

    private final SelectionKey selectionKey;
    private ConnectionState state;
    private final MessageBuffer outgoingMessages;
    private final MessageBuffer incomingMessages;
    private int subscriberId;
    private Instant lastActive;

    public Connection(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        this.state = ConnectionState.NOT_CONNECTED;
        this.subscriberId = -1;
        this.outgoingMessages = new MessageBuffer();
        this.incomingMessages = new MessageBuffer();
        this.lastActive = Instant.now();
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public ConnectionState getState() {
        return state;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public Connection setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public MessageBuffer getOutgoingMessages() {
        return outgoingMessages;
    }

    public MessageBuffer getIncomingMessages() {
        return incomingMessages;
    }

    public Instant getLastActive() {
        return lastActive;
    }

    public Connection setLastActive(Instant lastActive) {
        this.lastActive = lastActive;
        return this;
    }
}
