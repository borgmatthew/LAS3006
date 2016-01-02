package mt.edu.um.protocol.message;

/**
 * Created by matthew on 31/12/2015.
 */
public class DisconnectMessage implements Message {

    protected DisconnectMessage() {
    }

    @Override
    public byte[] build() {
        return new byte[0];
    }

    @Override
    public void resolve(byte[] messageInBytes) {
    }

    @Override
    public short getKey() {
        return MessageType.DISCONNECT.getId();
    }

    @Override
    public MessageType getType() {
        return MessageType.DISCONNECT;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
