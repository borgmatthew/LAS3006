package mt.edu.um;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.message.Message;

import java.nio.channels.SelectionKey;
import java.util.Optional;
import java.util.TimerTask;

/**
 * Created by matthew on 10/01/2016.
 */
public class ScheduledMessageGenerator extends TimerTask {

    private final MessageGenerator messageGenerator;
    private final Connection connection;

    protected ScheduledMessageGenerator(final MessageGenerator messageGenerator, Connection connection) {
        this.messageGenerator = messageGenerator;
        this.connection = connection;
    }

    @Override
    public void run() {
        Optional<Message> generatedMessage = messageGenerator.generate(connection);
        if (generatedMessage.isPresent()) {
            connection.getOutgoingMessages().add(generatedMessage.get());
            connection.getSelectionKey().interestOps(connection.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
            connection.getSelectionKey().selector().wakeup();
        }
    }
}
