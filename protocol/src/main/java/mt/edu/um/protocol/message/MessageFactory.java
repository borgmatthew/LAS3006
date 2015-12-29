package mt.edu.um.protocol.message;

/**
 * Created by matthew on 29/12/2015.
 */
public class MessageFactory {

    public static Message getMessageInstance(MessageType messageType) {
        switch (messageType) {
            case CONNECT: {
                return new ConnectMessage();
            }
            default: {
                throw new UnsupportedOperationException("not yet implemented");
            }
        }
    }
}
