package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class ConnAckMessage implements Message {

    private int id;
    private byte result;

    protected ConnAckMessage() {
        id = -1;
        result = (byte)0;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(5)
                .putInt(id)
                .put(result)
                .array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        id = buffer.getInt();
        result = buffer.get();
    }

    @Override
    public short getKey() {
        return MessageType.CONNACK.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.CONNACK;
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

    public ConnAckMessage setResult(boolean result) {
        this.result = (byte) (result ? 1 : 0);
        return this;
    }

    public boolean getResult() {
        return this.result == 0 ? false : true;
    }
}
