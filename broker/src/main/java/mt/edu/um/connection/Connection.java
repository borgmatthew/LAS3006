package mt.edu.um.connection;

import java.nio.channels.SelectionKey;

/**
 * Created by matthew on 02/01/2016.
 */
public class Connection {

    private final SelectionKey selectionKey;
    private ConnectionState state;
    private int subscriberId;

    public Connection(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
        this.state = ConnectionState.PENDING_CONNECTION;
        this.subscriberId = -1;
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
}
