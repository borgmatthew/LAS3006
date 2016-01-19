package mt.edu.um.protocol.communication;

import mt.edu.um.protocol.message.Message;
import mt.edu.um.protocol.message.MessageFactory;
import mt.edu.um.protocol.message.MessageType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by matthew on 27/12/2015.
 */
public class BrokerProtocolImpl implements BrokerProtocol {

    private final short HEADER_BYTES = 5;
    private final short BODY_BYTES = 1019;
    private final short BUFFER_SIZE = HEADER_BYTES + BODY_BYTES;
    private final Map<SocketChannel, Client> clients = new HashMap<>();

    @Override
    public void send(SocketChannel channel, Message message) throws IOException {
        if (!clients.containsKey(channel)) {
            clients.put(channel, new Client(BUFFER_SIZE, HEADER_BYTES));
        }

        Client client = clients.get(channel);
        ByteBuffer headerBuffer = client.getWriteHeaderBuffer();
        ByteBuffer bodyBuffer = client.getWriteBodyBuffer();
        ByteBuffer[] writeBuffers = new ByteBuffer[]{headerBuffer, bodyBuffer};

        byte[] messageInBytes = message.build();
        final int iterations = messageInBytes.length / BODY_BYTES + ((messageInBytes.length % BODY_BYTES > 0) ? 1 : 0);

        for (int i = 0; i < iterations; i++) {
            int startOffset = i * BODY_BYTES;
            short remainingLength = (short) (((i + 1) * BODY_BYTES > messageInBytes.length) ? messageInBytes.length - i * BODY_BYTES : BODY_BYTES);

            PacketHeader header = new PacketHeader(message.getKey(), remainingLength, i == iterations - 1);
            headerBuffer.put(header.build()).flip();
            bodyBuffer.put(messageInBytes, startOffset, remainingLength).flip();

            long writtenBytes = channel.write(writeBuffers);
            if(writtenBytes != headerBuffer.position() + bodyBuffer.position()) {
                clients.remove(channel);
                throw new IOException("Error while writing to socket");
            }
            headerBuffer.clear();
            bodyBuffer.clear();
        }
    }

    @Override
    public List<Message> receive(SocketChannel channel) throws IOException {
        if (!clients.containsKey(channel)) {
            clients.put(channel, new Client(BUFFER_SIZE, HEADER_BYTES));
        }

        Client client = clients.get(channel);
        ByteBuffer buffer = client.getReadBuffer();
        List<Message> messages = new ArrayList<>();

        long readBytes = channel.read(buffer);
        if (readBytes == -1) {
            clients.remove(channel);
            throw new IOException("Connection closed");
        }

        buffer.flip();
        while (buffer.hasRemaining() && (buffer.position() + HEADER_BYTES) < buffer.capacity()) {
            Optional<Packet> lastPacketOptional = client.getPacketBuffer().getLastPacket();
            if (lastPacketOptional.isPresent() && !lastPacketOptional.get().isComplete()) {
                Packet lastPacket = lastPacketOptional.get();
                int bytesToRead = lastPacket.getHeader().getRemainingLength() - lastPacket.getBody().length;
                byte[] remainingBytes = new byte[bytesToRead];
                buffer.get(remainingBytes);
                lastPacket.addData(remainingBytes);
            } else {
                PacketHeader header = parseHeader(buffer);
                byte[] packetData;
                if (header.getRemainingLength() <= buffer.limit() - buffer.position()) {
                    packetData = new byte[header.getRemainingLength()];
                } else {
                    packetData = new byte[buffer.limit() - buffer.position()];
                }
                buffer.get(packetData);
                Packet packet = new Packet(header, packetData);
                client.getPacketBuffer().push(packet);
            }

            lastPacketOptional = client.getPacketBuffer().getLastPacket();
            if (lastPacketOptional.isPresent() && lastPacketOptional.get().getHeader().getIsLastPacket() && lastPacketOptional.get().isComplete()) {
                List<Packet> packets = client.getPacketBuffer().getAllPackets();
                byte[] messageInBytes = joinArrays(packets);
                Message message = MessageFactory.getMessageInstance(getMessageType(packets.get(0).getHeader().getMessageKey()));
                message.resolve(messageInBytes);
                messages.add(message);
                client.getPacketBuffer().clear();
            }
        }

        if (buffer.hasRemaining()) {
            buffer.compact();
        } else {
            buffer.clear();
        }
        return messages;
    }

    private PacketHeader parseHeader(ByteBuffer buffer) {
        short messageTypeId = buffer.getShort();
        short remainingLength = buffer.getShort();
        byte isLastPacket = buffer.get();
        return new PacketHeader(messageTypeId, remainingLength, isLastPacket == 1);
    }

    private MessageType getMessageType(short messageTypeId) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getId() == messageTypeId) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Message not found");
    }

    private byte[] joinArrays(List<Packet> packets) {
        int size = 0;
        for (Packet packet : packets) {
            size += packet.getBody().length;
        }
        byte[] joinedArray = new byte[size];
        int startOffset = 0;
        for (Packet packet : packets) {
            System.arraycopy(packet.getBody(), 0, joinedArray, startOffset, packet.getBody().length);
            startOffset += packet.getBody().length;
        }
        return joinedArray;
    }
}
