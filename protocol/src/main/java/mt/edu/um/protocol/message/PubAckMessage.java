package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class PubAckMessage implements Message {

    int messageId;
    String topic;
    byte result;

    protected PubAckMessage() {
        messageId = -1;
        topic = "";
        result = (byte) 0;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(8 + topic.length())
                .putInt(messageId)
                .putInt(topic.length())
                .put(topic.getBytes())
                .put(result)
                .array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        messageId = buffer.getInt();
        byte[] topicBytes = new byte[buffer.getInt()];
        buffer.get(topicBytes);
        topic = new String(topicBytes);
        result = buffer.get();
    }

    @Override
    public short getKey() {
        return MessageType.PUBACK.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.PUBACK;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public PubAckMessage setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getTopic() {
        return topic;
    }

    public PubAckMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public boolean getResult() {
        return result == (byte) 1;
    }

    public PubAckMessage setResult(boolean result) {
        this.result = (byte) (result ? 1 : 0);
        return this;
    }
}
