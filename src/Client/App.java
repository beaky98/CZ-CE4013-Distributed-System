package src.Client;

import java.util.Scanner;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;

/**
 * Main application
 */
@Command(name = "app", description = "Starts the client for this app.", mixinStandardHelpOptions = true)
public class App implements Callable<Integer> {

    // Additional flags for CLI
    @Option(names = "--ip", description = "IP address of server to connect to.")
    String ip = "127.0.0.1";

    @Option(names = "--port", description = "Port number of server to connect to.")
    int port = 2222;

    @Option(names = "--timeout", description = "Time in milliseconds to wait before resending messages.")
    int timeout = 1000;

    @Option(names = "--noresend", description = "Flag to not resend messages if no response received.")
    boolean noResend = false;

    /**
     * Main class
     */
    public Integer call() throws Exception {

        try {
            // Creates new client instance
            Client client = new Client(ip, port);

            Scanner sc = new Scanner(System.in);

            while (true) {
                // Prints options
                String payload = BankInterface.printOptions();
                
                // End app if user enters 'bye'
                if (payload.equals("0")) {
                    BankInterface.exitMessage();
                    break;
                }

                String req = client.getReqId() + '_' + payload;

                // Waits for response from server
                String res = client.sendWithTimeout(req, timeout, noResend);
                if (res != null) {
                    System.out.println(res);
                } else {
                    System.out.println("No response received");
                }

                // Special case for monitorUpdate:
                // Stops the program and just waits for any updates
                if (payload.charAt(0) == '5') {
                    int duration = Integer.parseInt(payload.split("_")[1]);
                    client.receiveAll(duration);
                }

            }
            sc.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    /**
     * Calls main function with CLI arguments
     * @param args Additional CLI arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
