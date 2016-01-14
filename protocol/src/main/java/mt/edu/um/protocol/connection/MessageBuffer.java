package mt.edu.um.protocol.connection;

import mt.edu.um.protocol.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 03/01/2016.
 */
public class MessageBuffer {

    private ArrayList<Message> messages;
    private int totalMessagesBuffered;
    public MessageBuffer() {
        messages = new ArrayList<>();
        totalMessagesBuffered = 0;
    }

    public void add(Message message) {
        synchronized (this) {
            this.messages.add(message);
            this.totalMessagesBuffered += 1;
        }
    }

    public void addAll(List<Message> messages) {
        synchronized (this) {
            this.messages.addAll(messages);
            this.totalMessagesBuffered += messages.size();
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

    public int getTotalMessagesBuffered() {
        return totalMessagesBuffered;
    }
}
