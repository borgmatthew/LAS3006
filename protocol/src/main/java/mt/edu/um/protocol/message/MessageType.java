package mt.edu.um.protocol.message;

/**
 * Created by matthew on 26/11/2015.
 */
public enum MessageType {
    CONNECT     ((short)0, "CONNECT"),
    CONNACK     ((short)1, "CONNACK"),
    SUBSCRIBE   ((short)2, "SUBSCRIBE"),
    SUBACK      ((short)3, "SUBACK"),
    PINGREQ     ((short)4, "PINGREQ"),
    PINGRESP    ((short)5, "PINGRESP"),
    PUBLISH     ((short)6, "PUBLISH"),
    PUBACK      ((short)7, "PUBACK"),
    PUBREC      ((short)8, "PUBREC"),
    UNSUBSCRIBE ((short)9, "UNSUBSCRIBE"),
    UNSUBACK    ((short)10, "UNSUBACK"),
    DISCONNECT  ((short)11, "DISCONNECT");

    private final String message;
    private final short id;

    MessageType(short messageId, String message) {
        this.message = message;
        this.id = messageId;
    }

    public String getMessage() {
        return message;
    }

    public short getId() {
        return id;
    }
}
