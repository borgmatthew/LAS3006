package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class PublishMessage implements Message {

    private int messageId;
    private String topic;
    private String payload;

    protected PublishMessage() {
        messageId = -1;
        topic = "";
        payload = "";
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(12 + topic.length() + payload.length())
                .putInt(messageId)
                .putInt(topic.length())
                .put(topic.getBytes())
                .putInt(payload.length())
                .put(payload.getBytes())
                .array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        messageId = buffer.getInt();
        byte[] topicBytes = new byte[buffer.getInt()];
        buffer.get(topicBytes);
        topic = new String(topicBytes);
        byte[] payloadBytes = new byte[buffer.getInt()];
        buffer.get(payloadBytes);
        payload = new String(payloadBytes);
    }

    @Override
    public short getKey() {
        return MessageType.PUBLISH.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.PUBLISH;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public PublishMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public PublishMessage setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public PublishMessage setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }
}
