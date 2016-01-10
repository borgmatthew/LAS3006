package mt.edu.um;

import java.util.Arrays;

/**
 * Created by matthew on 16/12/2015.
 */
public class Launcher {

    private final static String SERVER_IP = "-serverIp=";
    private final static String SERVER_PORT = "-serverPort=";
    private final static String PUBLISH_TOPIC = "-publishTopic=";
    private final static String MESSAGE_FREQUENCY = "-messageFrequency=";


    public static void main(String[] args) {
        Options options = new Options(args);
        Client client = new Client(new PublisherMessageGenerator(options.getTopic()), options.getMessageFrequency());
        client.run(options.getServerIp(), options.getServerPort());
    }

    private static class Options {

        private String serverIp;
        private int serverPort;
        private String topic;
        private int messageFrequency;

        public Options(String args[]) {
            parseOptions(args);
        }

        void parseOptions(String[] args) {
            serverIp = Arrays.stream(args)
                    .filter(param -> param.startsWith(SERVER_IP))
                    .findFirst()
                    .get()
                    .substring(SERVER_IP.length());

            serverPort = Integer.parseInt(Arrays.stream(args)
                    .filter(param -> param.startsWith(SERVER_PORT))
                    .findFirst()
                    .get()
                    .substring(SERVER_PORT.length()));

            topic = Arrays.stream(args)
                    .filter(param -> param.startsWith(PUBLISH_TOPIC))
                    .findFirst()
                    .get()
                    .substring(PUBLISH_TOPIC.length());

            messageFrequency = Integer.parseInt(Arrays.stream(args)
                    .filter(param -> param.startsWith(MESSAGE_FREQUENCY))
                    .findFirst()
                    .get()
                    .substring(MESSAGE_FREQUENCY.length()));

            String serverIpProperty = System.getProperty("serverIp");
            if(serverIpProperty != null) {
                serverIp = serverIpProperty;
            }

            String serverPortProperty = System.getProperty("serverPort");
            if(serverPortProperty != null) {
                serverPort = Integer.parseInt(serverPortProperty);
            }

            String topicProperty = System.getProperty("publishTopic");
            if(topicProperty != null) {
                topic = topicProperty;
            }

            String messageFrequencyProperty = System.getProperty("messageFrequency");
            if(messageFrequencyProperty != null) {
                serverPort = Integer.parseInt(messageFrequencyProperty);
            }
        }

        public String getServerIp() {
            return serverIp;
        }

        public String getTopic() {
            return topic;
        }

        public int getMessageFrequency() {
            return messageFrequency;
        }

        public int getServerPort() {
            return serverPort;
        }
    }
}
