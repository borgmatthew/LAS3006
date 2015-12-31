package mt.edu.um.protocol.message;

/**
 * Created by matthew on 28/12/2015.
 */
public interface Visitor {
    void visit(ConnectMessage connectMessage);
    void visit(ConnAckMessage connAckMessage);
    void visit(SubscribeMessage subscribeMessage);
    void visit(SubAckMessage subAckMessage);
    void visit(PingReqMessage pingReqMessage);
    void visit(PingRespMessage pingRespMessage);
    void visit(PublishMessage publishMessage);
    void visit(PubAckMessage pubAckMessage);
    void visit(PubRecMessage pubRecMessage);
}
