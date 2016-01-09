package mt.edu.um;

/**
 * Created by matthew on 09/01/2016.
 */
public class Launcher {

    public static void main(String[] args) {
        Client client = new Client(new SubscriberMessageGenerator());
        client.run("127.0.0.1", 3523);
    }
}
