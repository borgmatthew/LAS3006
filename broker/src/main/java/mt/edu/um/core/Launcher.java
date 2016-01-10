package mt.edu.um.core;

import java.util.Arrays;

/**
 * Created by matthew on 15/12/2015.
 */
public class Launcher {

    private final static String SERVER_PORT = "-serverPort=";
    private final static String MAX_INACTIVE_MINUTES = "-maxInactiveMinutes=";

    public static void main(String[] args) {
        Options options = new Options(args);
        Server server = new Server(options.getPort(), options.getMaxInactiveMinutes());
        server.start();
    }

    private static class Options {

        private int maxInactiveMinutes;
        private int port;

        public Options(String args[]) {
            parseOptions(args);
        }

        void parseOptions(String[] args) {
            maxInactiveMinutes = Integer.parseInt(Arrays.stream(args)
                    .filter(param -> param.startsWith(MAX_INACTIVE_MINUTES))
                    .findFirst()
                    .get()
                    .substring(MAX_INACTIVE_MINUTES.length()));

            port = Integer.parseInt(Arrays.stream(args)
                    .filter(param -> param.startsWith(SERVER_PORT))
                    .findFirst()
                    .get()
                    .substring(SERVER_PORT.length()));

            String serverPortProperty = System.getProperty("serverPort");
            if(serverPortProperty != null) {
                port = Integer.parseInt(serverPortProperty);
            }

            String maxInactiveMinutesProperty = System.getProperty("maxInactiveMinutes");
            if(maxInactiveMinutesProperty != null) {
                maxInactiveMinutes = Integer.parseInt(maxInactiveMinutesProperty);
            }
        }

        public int getMaxInactiveMinutes() {
            return maxInactiveMinutes;
        }

        public int getPort() {
            return port;
        }
    }
}
