package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 28/12/2015.
 */
public class MessageHeader {

    MessageType type;

    short remainingLength;

    public MessageHeader(MessageType type, short remainingLength) {
        this.type = type;
        this.remainingLength = remainingLength;
    }

    public MessageType getType() {
        return type;
    }

    public long getRemainingLength() {
        return remainingLength;
    }

    public byte[] build() {
        return ByteBuffer.allocate(4).putShort(type.getId()).putShort(remainingLength).array();
    }
}
