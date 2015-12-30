package mt.edu.um.protocol.communication;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 28/12/2015.
 */
public class PacketHeader {

    private short key;
    private short remainingLength;
    private byte isLastPacket;

    protected PacketHeader(short key, short remainingLength, boolean isLastPacket) {
        this.key = key;
        this.remainingLength = remainingLength;
        this.isLastPacket = isLastPacket ? (byte)1 : (byte)0;
    }

    public short getMessageKey() {
        return key;
    }

    public boolean getIsLastPacket() {
        return isLastPacket == 1;
    }

    public short getRemainingLength() {
        return remainingLength;
    }

    public byte[] build() {
        return ByteBuffer.allocate(5)
                .putShort(key)
                .putShort(remainingLength)
                .put(isLastPacket)
                .array();
    }
}
