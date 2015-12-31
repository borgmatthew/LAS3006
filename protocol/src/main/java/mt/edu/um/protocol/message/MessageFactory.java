package mt.edu.um.protocol.message;

/**
 * Created by matthew on 29/12/2015.
 */
public class MessageFactory {

    public static Message getMessageInstance(MessageType messageType) {
        switch (messageType) {
            case CONNECT: {
                return new ConnectMessage();
            }
            case CONNACK: {
                return new ConnAckMessage();
            }
            case DISCONNECT: {
                return new DisconnectMessage();
            }
            case PINGREQ: {
                return new PingReqMessage();
            }
            case PINGRESP: {
                return new PingRespMessage();
            }
            case PUBACK: {
                return new PubAckMessage();
            }
            case PUBLISH: {
                return new PublishMessage();
            }
            case PUBREC: {
                return new PubRecMessage();
            }
            case SUBACK: {
                return new SubAckMessage();
            }
            case SUBSCRIBE: {
                return new SubscribeMessage();
            }
            case UNSUBACK: {
                return new UnsubAckMessage();
            }
            case UNSUBSCRIBE: {
                return new UnsubscribeMessage();
            }
            default: {
                throw new UnsupportedOperationException("not yet implemented");
            }
        }
    }
}
