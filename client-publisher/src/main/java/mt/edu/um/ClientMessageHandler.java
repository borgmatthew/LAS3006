package mt.edu.um;

import mt.edu.um.protocol.message.*;

/**
 * Created by matthew on 03/01/2016.
 */
public class ClientMessageHandler {

    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    public void handleMessage(Message message) {
        switch (message.getType()) {
            case CONNACK: {
                handle((ConnAckMessage) message);
                break;
            }
            case PUBACK: {
                handle((PubAckMessage) message);
                break;
            }
        }
    }

    private void handle(PubAckMessage message) {
        System.out.println(message.getType() + ": " + message.getTopic()
        + "\nMESSAGE: " + message.getMessageId()
        + "\nRESULT: OK");
    }

    private void handle(ConnAckMessage message) {
        System.out.println(message.getType() + ": " + message.getId());
        if(message.getResult()) {
            System.out.println("RESULT: OK");
            isConnected = true;
        } else {
            System.out.println("RESULT: ERROR!");
        }
    }
}
