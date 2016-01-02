package mt.edu.um.core;

import mt.edu.um.connection.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthew on 02/01/2016.
 */
public class SubscriberConnections {

    private final Map<Integer, Connection> subscriberConnections;

    public SubscriberConnections() {
        this.subscriberConnections = new HashMap<>();
    }

    public void add(int subscriberId, Connection connection) {
        subscriberConnections.put(subscriberId, connection);
    }

    public Connection get(int subscriberId) {
        return this.subscriberConnections.get(subscriberId);
    }

    public void remove(int subscriberId) {
        remove(subscriberId);
    }
}
