package mt.edu.um;

import mt.edu.um.protocol.connection.Connection;
import mt.edu.um.protocol.connection.ConnectionState;
import mt.edu.um.protocol.message.*;

import java.nio.channels.SelectionKey;

/**
 * Created by matthew on 03/01/2016.
 */
public class ClientMessageHandler implements Visitor {

    private final Connection connection;

    public ClientMessageHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void visit(ConnectMessage connectMessage) {

    }

    @Override
    public void visit(ConnAckMessage connAckMessage) {
        System.out.println(connAckMessage.getType() + ": " + connAckMessage.getId()
                + "\nRESULT: " + (connAckMessage.getResult() ? "OK" : "ERROR")
                + "\n");
        if (connAckMessage.getResult()) {
            connection.setState(ConnectionState.CONNECTED);
            connection.setSubscriberId(connAckMessage.getId());
        } else {
            connection.setState(ConnectionState.NOT_CONNECTED);
        }
    }

    @Override
    public void visit(SubscribeMessage subscribeMessage) {

    }

    @Override
    public void visit(SubAckMessage subAckMessage) {
        System.out.println(subAckMessage.getType() + ": " + subAckMessage.getTopic()
                + "\nRESULT: " + (subAckMessage.getResult() ? "OK" : "ERROR")
                + "\n");
    }

    @Override
    public void visit(PingReqMessage pingReqMessage) {

    }

    @Override
    public void visit(PingRespMessage pingRespMessage) {
        System.out.println(pingRespMessage.getType() + ": " + pingRespMessage.getMessageId() + "\n");
    }

    @Override
    public void visit(PublishMessage publishMessage) {
        System.out.println(publishMessage.getType() + ": " + publishMessage.getTopic()
                + "\nMESSAGE: " + publishMessage.getMessageId()
                + "\nPAYLOAD: " + publishMessage.getPayload()
                + "\n");
        PubRecMessage pubRecMessage = (PubRecMessage) MessageFactory.getMessageInstance(MessageType.PUBREC);
        pubRecMessage.setClientId(connection.getSubscriberId())
                .setMessageId(publishMessage.getMessageId())
                .setTopic(publishMessage.getTopic())
                .setResult(true);
        connection.getOutgoingMessages().add(pubRecMessage);
        connection.getSelectionKey().interestOps(connection.getSelectionKey().interestOps() | SelectionKey.OP_WRITE);
        connection.getSelectionKey().selector().wakeup();
    }

    @Override
    public void visit(PubAckMessage pubAckMessage) {
        System.out.println(pubAckMessage.getType() + ": " + pubAckMessage.getTopic()
                + "\nMESSAGE: " + pubAckMessage.getMessageId()
                + "\nRESULT: " + pubAckMessage.getResult()
                + "\n");
    }

    @Override
    public void visit(PubRecMessage pubRecMessage) {

    }

    @Override
    public void visit(UnsubscribeMessage unsubscribeMessage) {

    }

    @Override
    public void visit(UnsubAckMessage unsubAckMessage) {
        System.out.println(unsubAckMessage.getType() + ": " + unsubAckMessage.getTopic()
                + "\nRESULT: " + unsubAckMessage.getResult() + "\n");
    }

    @Override
    public void visit(DisconnectMessage disconnectMessage) {

    }
}
