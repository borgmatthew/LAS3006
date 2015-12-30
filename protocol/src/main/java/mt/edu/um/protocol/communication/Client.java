package mt.edu.um.protocol.communication;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 30/12/2015.
 */
public class Client {

    private final ByteBuffer readBuffer;
    private final ByteBuffer writeBodyBuffer;
    private final PacketBuffer packetBuffer;
    private final ByteBuffer writeHeaderBuffer;

    protected Client(int totalBufferSize, int headerBufferSize) {
        readBuffer = ByteBuffer.allocate(totalBufferSize);
        writeBodyBuffer = ByteBuffer.allocate(totalBufferSize - headerBufferSize);
        writeHeaderBuffer = ByteBuffer.allocate(headerBufferSize);
        packetBuffer = new PacketBuffer();
    }

    public ByteBuffer getReadBuffer() {
        return readBuffer;
    }

    public ByteBuffer getWriteBodyBuffer() {
        return writeBodyBuffer;
    }

    public PacketBuffer getPacketBuffer() {
        return packetBuffer;
    }

    public ByteBuffer getWriteHeaderBuffer() {
        return writeHeaderBuffer;
    }
}
