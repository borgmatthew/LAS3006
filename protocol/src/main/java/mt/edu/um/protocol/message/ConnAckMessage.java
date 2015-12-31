package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class ConnAckMessage implements Message {

    int id;

    protected ConnAckMessage() {
        id = -1;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(4).putInt(id).array();
    }

    @Override
    public void resolve(byte[] buffer) {
        id = ByteBuffer.wrap(buffer).getInt();
    }

    @Override
    public short getKey() {
        return MessageType.CONNACK.getId();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public int getId() {
        return id;
    }

    public ConnAckMessage setId(int id) {
        this.id = id;
        return this;
    }
}
