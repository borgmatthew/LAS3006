package mt.edu.um.core;

import mt.edu.um.connection.Connection;
import mt.edu.um.protocol.message.Message;

/**
 * Created by matthew on 02/01/2016.
 */
public class Response {

    private final Message message;
    private final Connection connection;

    public Response(Message message, Connection connection) {
        this.message = message;
        this.connection = connection;
    }

    public Message getMessage() {
        return message;
    }

    public Connection getConnection() {
        return connection;
    }
}
