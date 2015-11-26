package mt.edu.um.protocol;

/**
 * Created by matthew on 26/11/2015.
 */
public enum Message {
    CONNECT     ("CONNECT"),
    CONNACK     ("CONNACK"),
    SUBSCRIBE   ("SUBSCRIBE"),
    SUBACK      ("SUBACK"),
    PINGREQ     ("PINGREQ"),
    PINGRESP    ("PINGRESP"),
    PUBLISH     ("PUBLISH"),
    PUBACK      ("PUBACK"),
    PUBREC      ("PUBREC"),
    UNSUBSCRIBE ("UNSUBSCRIBE"),
    UNSUBACK    ("UNSUBACK"),
    DISCONNECT  ("DISCONNECT");

    private String message;

    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
