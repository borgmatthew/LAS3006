package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class UnsubAckMessage implements Message {

    private String topic;
    private byte result;

    protected UnsubAckMessage() {
        topic = "";
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(5 + topic.length())
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
        return MessageType.UNSUBACK.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.UNSUBACK;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getTopic() {
        return topic;
    }

    public UnsubAckMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public boolean getResult() {
        return result != (byte) 0;
    }

    public UnsubAckMessage setResult(boolean result) {
        this.result = (byte) (result ? 1 : 0);
        return this;
    }
}
