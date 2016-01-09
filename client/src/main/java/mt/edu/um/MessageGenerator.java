package mt.edu.um;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;

import java.util.Optional;

/**
 * Created by matthew on 09/01/2016.
 */
public interface MessageGenerator {

    Optional<Message> generate(final Connection connection);

}
