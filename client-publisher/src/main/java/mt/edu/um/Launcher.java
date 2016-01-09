package mt.edu.um;

/**
 * Created by matthew on 16/12/2015.
 */
public class Launcher {

    public static void main(String[] args) {
        Client client = new Client(new PublisherMessageGenerator("/home/kitchen/fridge/temperature"));
        client.run("127.0.0.1", 3523);
    }
}
