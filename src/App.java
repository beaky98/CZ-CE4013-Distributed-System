package src;

import java.util.Scanner;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(name = "app", description = "Does cool stuff.", mixinStandardHelpOptions = true)
public class App implements Callable<Integer> {

    @Option(names = "--ip", description = "server ip")
    String ip = "127.0.0.1";

    @Option(names = "--port", description = "port number")
    int port = 1234;

    @Option(names = "--timeout", description = "timeout")
    int timeout = 1000;

    @Option(names = "--resend", description = "resend")
    boolean resend = true;

    public Integer call() throws Exception {
        System.out.println("Test");
        try {
            // Creates new client instance
            Client client = new Client(ip, port);

            Scanner sc = new Scanner(System.in);
            System.out.println("Starting loop... type 'bye' to exit...");

            while (true) {

                // TODO: Get request from banking service
                String msg = sc.nextLine();

                // TODO: Marshal request
                String req = client.getReqId() + '_' + msg;

                // Waits for response from server
                String res = client.sendWithTimeout(req, timeout, resend);
                if (res != null) {
                    System.out.println(res);
                } else {
                    System.out.println("No response received");
                }

                // End app if user enters 'bye'
                if (msg.equals("bye"))
                    break;
            }
            sc.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }
    public static void main(String[] args) {
        // in 1 line, parse the args, handle errors,
        // handle requests for help/version info, call the business logic
        // and obtain an exit status code:

        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
