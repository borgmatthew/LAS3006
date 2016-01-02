package mt.edu.um.protocol.message;

import java.nio.ByteBuffer;

/**
 * Created by matthew on 31/12/2015.
 */
public class PingReqMessage implements Message {

    int messageId;

    protected PingReqMessage() {
        messageId = -1;
    }

    @Override
    public byte[] build() {
        return ByteBuffer.allocate(4).putInt(messageId).array();
    }

    @Override
    public void resolve(byte[] buffer) {
        messageId = ByteBuffer.wrap(buffer).getInt();
    }

    @Override
    public short getKey() {
        return MessageType.PINGREQ.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.PINGREQ;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public int getMessageId() {
        return messageId;
    }

    public PingReqMessage setMessageId(int messageId) {
        this.messageId = messageId;
        return this;
    }
}
