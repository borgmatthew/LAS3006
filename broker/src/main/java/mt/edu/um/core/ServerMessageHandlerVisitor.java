package mt.edu.um.core;

import mt.edu.um.protocol.message.ConnAckMessage;
import mt.edu.um.protocol.message.ConnectMessage;
import mt.edu.um.protocol.message.Visitor;

/**
 * Created by matthew on 28/12/2015.
 */
public class ServerMessageHandlerVisitor implements Visitor {
    @Override
    public void visit(ConnectMessage connectMessage) {
        System.out.println("Received connection. Client id: " + connectMessage.getId());
    }

    @Override
    public void visit(ConnAckMessage connAckMessage) {

    }
}
