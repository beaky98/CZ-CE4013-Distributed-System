package src.Client;

import java.util.Scanner;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(name = "app", description = "Does cool stuff.", mixinStandardHelpOptions = true)
public class App implements Callable<Integer> {

    @Option(names = "--ip", description = "server ip")
    String ip = "127.0.0.1";

    @Option(names = "--port", description = "port number")
    int port = 2222;

    @Option(names = "--timeout", description = "timeout")
    int timeout = 1000;

    @Option(names = "--resend", description = "resend")
    boolean resend = true;

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

                // TODO: Marshal request
                String req = client.getReqId() + '_' + payload;

                // Waits for response from server
                String res = client.sendWithTimeout(req, timeout, resend);
                if (res != null) {
                    System.out.println(res);
                } else {
                    System.out.println("No response received");
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
