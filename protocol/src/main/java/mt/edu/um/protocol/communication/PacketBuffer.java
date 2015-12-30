package mt.edu.um.protocol.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by matthew on 30/12/2015.
 */
public class PacketBuffer {

    List<Packet> packets;

    protected PacketBuffer() {
        this.packets = new ArrayList<>();
    }

    public void push(Packet packet) {
        packets.add(packet);
    }

    public List<Packet> getAllPackets() {
        return packets;
    }

    public Optional<Packet> getLastPacket() {
        if(packets.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(packets.get(packets.size() - 1));
        }
    }

    public void clear() {
        packets.clear();
    }
}
