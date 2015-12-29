package mt.edu.um.protocol.communication;

import mt.edu.um.protocol.message.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by matthew on 27/12/2015.
 */
public class BrokerProtocolImpl implements BrokerProtocol {

    private final short HEADER_BYTES = 4;
    private final short BODY_BYTES = 1024;

    @Override
    public void send(SocketChannel channel, Message message) {
        byte [] messageInBytes = message.build();
        short messageKey = message.getKey();
        final int iterations = messageInBytes.length / BODY_BYTES + ((messageInBytes.length % BODY_BYTES > 0) ? 1 : 0);
        for(int i=0; i < iterations; i++) {
            int startOffset = i * BODY_BYTES;
            short remainingLength = (short) (((i+1)*BODY_BYTES > messageInBytes.length) ? messageInBytes.length - i * BODY_BYTES : BODY_BYTES);
            MessageHeader header = new MessageHeader(messageKey, remainingLength);
            try {
                channel.write(new ByteBuffer[] { ByteBuffer.wrap(header.build()), ByteBuffer.wrap(messageInBytes, startOffset, remainingLength) });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Message receive(SocketChannel channel) {
        ByteBuffer headerBuffer = ByteBuffer.allocate(HEADER_BYTES);
        ByteBuffer bodyBuffer = ByteBuffer.allocate(BODY_BYTES);
        try {
            long readBytes = channel.read(new ByteBuffer[]{headerBuffer, bodyBuffer});
            headerBuffer.flip();
            bodyBuffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageHeader header = parseHeader(headerBuffer);
        Message message = MessageFactory.getMessageInstance(getMessageType(header.getMessageKey()));
        message.resolve(bodyBuffer.array());
        return message;
    }

    private MessageHeader parseHeader(ByteBuffer buffer) {
        short messageTypeId = buffer.getShort();
        short remainingLength = buffer.getShort();
        return new MessageHeader(messageTypeId, remainingLength);
    }

    private MessageType getMessageType(short messageTypeId) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getId() == messageTypeId) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Message not found");
    }
}
