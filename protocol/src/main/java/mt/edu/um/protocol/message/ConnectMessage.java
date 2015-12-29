package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 28/12/2015.
 */
public class ConnectMessage implements Message {

    int id;

    protected ConnectMessage() {
        id = -1;
    }

    public int getId() {
        return id;
    }

    public ConnectMessage setId(int id) {
        this.id = id;
        return this;
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
    public short getKey() {
        return MessageType.CONNECT.getId();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
