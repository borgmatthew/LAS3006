package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class SubscribeMessage implements Message {

    String topic;

    protected SubscribeMessage() {
        topic = "";
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(4 + topic.length())
                .putInt(topic.length())
                .put(topic.getBytes())
                .array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        byte[] topicBytes = new byte[buffer.getInt()];
        buffer.get(topicBytes);
        topic = new String(topicBytes);
    }

    @Override
    public short getKey() {
        return MessageType.SUBSCRIBE.getId();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getTopic() {
        return topic;
    }

    public SubscribeMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }
}
