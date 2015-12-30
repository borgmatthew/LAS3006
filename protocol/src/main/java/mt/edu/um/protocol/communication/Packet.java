package mt.edu.um.protocol.communication;

/**
 * Created by matthew on 30/12/2015.
 */
public class Packet {

    private final PacketHeader header;
    private byte[] body;
    private boolean isComplete;

    protected Packet(PacketHeader header, byte[] body) {
        this.header = header;
        this.body = body;
        this.isComplete = body.length == header.getRemainingLength() ? true : false;
    }

    public PacketHeader getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean addData(byte[] data) {
        if (!isComplete) {
            byte[] temp = body;
            body = new byte[body.length + data.length];
            System.arraycopy(temp, 0, body, 0, temp.length);
            System.arraycopy(data, 0, body, temp.length, data.length);
            if(header.getRemainingLength() == body.length) {
                isComplete = true;
            }
            return true;
        } else {
            return false;
        }
    }
}
