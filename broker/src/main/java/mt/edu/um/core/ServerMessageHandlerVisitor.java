package mt.edu.um.core;

import mt.edu.um.protocol.message.*;

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

    @Override
    public void visit(SubscribeMessage subscribeMessage) {

    }

    @Override
    public void visit(SubAckMessage subAckMessage) {

    }

    @Override
    public void visit(PingReqMessage pingReqMessage) {

    }

    @Override
    public void visit(PingRespMessage pingRespMessage) {

    }

    @Override
    public void visit(PublishMessage publishMessage) {

    }

    @Override
    public void visit(PubAckMessage pubAckMessage) {

    }

    @Override
    public void visit(PubRecMessage pubRecMessage) {

    }

    @Override
    public void visit(UnsubscribeMessage unsubscribeMessage) {

    }

    @Override
    public void visit(UnsubAckMessage unsubAckMessage) {

    }

    @Override
    public void visit(DisconnectMessage disconnectMessage) {

    }
}
