package mt.edu.um;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;

/**
 * Created by matthew on 09/01/2016.
 */
public interface MessageGenerator {

    Message generate(final Connection connection);

}
