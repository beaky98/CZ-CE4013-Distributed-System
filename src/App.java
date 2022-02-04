package src;

import java.util.Scanner;

public class App {
    public static void main(String[] args)
    {
        try
        {
            // Address of server
            String ip = "127.0.0.1";
            int port = 1234;

            // Creates new client instance
            Client client = new Client(ip, port);
            
            int timeout = 1000;
            Scanner sc = new Scanner(System.in);

            System.out.println("Starting loop... type 'bye' to exit...");

            // loop while user not enters "bye"
            while (true)
            {
                // Gets input from command line
                String msg = sc.nextLine();

                // // Send msg to server
                // client.send(msg);

                // // Wait for server response
                // String res = client.receive();

                String res = client.sendWithTimeout(msg, timeout);
                if (res != null) {
                    System.out.println(res);
                }
                else {
                    System.out.println("No response received");
                }

                // break the loop if user enters "bye"
                if (msg.equals("bye"))
                    break;
            }
            sc.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
