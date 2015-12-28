package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 28/12/2015.
 */
public class ConnectMessage implements Message {

    int id;

    public ConnectMessage() {
        id = -1;
    }

    public ConnectMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(4).putInt(id).array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        id = buffer.getInt();
    }

    @Override
    public MessageType getType() {
        return MessageType.CONNECT;
    }
}
