package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class PubRecMessage implements Message {

    int messageId;
    int clientId;
    String topic;
    byte result;

    protected PubRecMessage() {
        messageId = -1;
        topic = "";
        clientId = -1;
        result = (byte) 0;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(12 + topic.length())
                .putInt(topic.length())
                .put(topic.getBytes())
                .putInt(clientId)
                .putInt(messageId)
                .put(result)
                .array();
    }

    @Override
    public void resolve(byte[] messageInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(messageInBytes);
        byte[] topicBytes = new byte[buffer.getInt()];
        buffer.get(topicBytes);
        topic = new String(topicBytes);
        clientId = buffer.getInt();
        messageId = buffer.getInt();
        result = buffer.get();
    }

    @Override
    public short getKey() {
        return MessageType.PUBREC.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.PUBREC;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public PubRecMessage setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

    public PubRecMessage setClientId(int clientId) {
        this.clientId = clientId;
        return this;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getTopic() {
        return topic;
    }

    public PubRecMessage setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getClientId() {
        return clientId;
    }

    public boolean getResult() {
        return result == (byte) 1;
    }

    public PubRecMessage setResult(boolean result) {
        this.result = (byte) (result ? 1 : 0);
        return this;
    }
}
