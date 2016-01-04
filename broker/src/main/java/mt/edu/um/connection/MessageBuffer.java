package mt.edu.um.connection;

import mt.edu.um.protocol.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 03/01/2016.
 */
public class MessageBuffer {

    private ArrayList<Message> messages;

    public MessageBuffer() {
        messages = new ArrayList<>();
    }

    public void add(Message message) {
        synchronized (this) {
            messages.add(message);
        }
    }

    public void addAll(List<Message> messages) {
        synchronized (this) {
            this.messages.addAll(messages);
        }
    }

    public List<Message> emptyBuffer() {
        List<Message> result;
        synchronized (this) {
            result = messages;
            messages = new ArrayList<>();
        }
        return result;
    }

    public Message getNext() {
        synchronized (this) {
            return this.messages.remove(0);
        }
    }
}
