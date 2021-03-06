package mt.edu.um.protocol.message;

/**
 * Created by matthew on 22/12/2015.
 */
public interface Message {

    /**
     * Generates a message
     */
    byte[] build();

    /**
     * Resolves a message
     */
    void resolve(byte[] buffer);

    short getKey();

    MessageType getType();

    void accept(Visitor visitor);
}
