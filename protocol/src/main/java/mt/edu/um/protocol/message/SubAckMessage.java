package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class SubAckMessage implements Message {

    private String topic;
    private byte result;

    protected SubAckMessage() {
        topic = "";
        result = (byte) 0;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(4 + topic.length())
                .putInt(topic.length())
                .put(topic.getBytes())
                .put(result)
                .array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        byte[] topicBytes = new byte[buffer.getInt()];
        buffer.get(topicBytes);
        topic = new String(topicBytes);
        result = buffer.get();
    }

    @Override
    public short getKey() {
        return MessageType.SUBACK.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.SUBACK;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getTopic() {
        return topic;
    }

    public SubAckMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public boolean getResult() {
        return result == 0 ? false : true;
    }

    public SubAckMessage setResult(boolean result) {
        this.result = (byte) (result ? 1 : 0);
        return this;
    }
}
