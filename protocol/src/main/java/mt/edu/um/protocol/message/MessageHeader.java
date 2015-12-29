package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 28/12/2015.
 */
public class MessageHeader {

    short key;

    short remainingLength;

    public MessageHeader(short key, short remainingLength) {
        this.key = key;
        this.remainingLength = remainingLength;
    }

    public short getMessageKey() {
        return key;
    }

    public long getRemainingLength() {
        return remainingLength;
    }

    public byte[] build() {
        return ByteBuffer.allocate(4).putShort(key).putShort(remainingLength).array();
    }
}
