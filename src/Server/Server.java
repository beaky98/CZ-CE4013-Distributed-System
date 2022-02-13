package src;

// MERGED WITH UDPSERVER?

// Java program to illustrate Server side
// Implementation using DatagramSocket
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(name = "server", description = "Does cool stuff.", mixinStandardHelpOptions = true)
public class Server implements Callable<Integer> {

    @Option(names = "--port", description = "port number")
	int server_port = 1234;

    @Option(names = "--loss", description = "percentage of packet loss")
	double loss_rate = 0.2;
	
    @Option(names = "--once", description = "at-most-once invocation semantic")
	boolean at_most_once = true;
	
	private DatagramSocket ds;
	private HashMap<String, String> reqList;

	public Integer call() throws Exception {
		// Create socket
		ds = new DatagramSocket(server_port);

		// Store request id / response pairs
		reqList = new HashMap<String, String>();

		System.out.printf("Running server on port %s with loss rate of %.3f...\n", server_port, loss_rate);

		while (true) {
			// Create and receive packet
			DatagramPacket packet = receive();

			String req = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Client: " + req);

			// TODO: Unmarshal request to get request id
			String[] reqArr = req.split("_", 2);
			for (int i=0; i<2; i++)
				System.out.println(reqArr[i]);
			String reqId = reqArr[0];

			// Checks if request has been sent before
			String res = checkReqId(reqId);

			// Get client address and port
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();

			// New request
			if (res == null) {
				// TODO: Unmarshal request to get request type
				// TODO: Banking Service perfoms request and returns response
				// TODO: Marshal response
				res = "Got message: " + reqArr[1];

				// Stores response
				storeRes(reqId, res);
			}

			if (Math.random() > loss_rate) {
				send(res, ip, port);
			}
			else {
				System.out.println("Dropping packet...");
			}

			// Exit the server if the client sends "bye"
			if (req.substring(req.length() - 3).equals("bye")) {
				System.out.println("Client sent bye.....EXITING");
				break;
			}
		}
		ds.close();
		return 0;
	}

	/**
	 * Sends a message to client
	 * 
	 * @param msg  Message to be sent
	 * @param ip   IP address of client
	 * @param port Port number of client
	 * @throws IOException
	 */
	private void send(String msg, InetAddress ip, int port) throws IOException {
		byte[] buf = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
		ds.send(packet);
	}

	/**
	 * Receives message from client
	 * 
	 * @return Message from client
	 * @throws IOException
	 */
	private DatagramPacket receive() throws IOException {
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		ds.receive(packet);

		return packet;
	}

	/**
	 * Searches if request has been made before
	 * If yes, return previous response
	 * 
	 * @param reqId Request id
	 * @return Response
	 */
	private String checkReqId(String reqId) {
		if (at_most_once && reqList.containsKey(reqId)) {
			System.out.println("Retrieving stored response");
			return reqList.get(reqId);
		}
		return null;
	}

	/**
	 * Stores request id and its response
	 * 
	 * @param reqId Request id
	 * @param response Response
	 */
	private void storeRes(String reqId, String response) {
		if (at_most_once)
			reqList.put(reqId, response);
	}

	public static void main(String[] args) {
        int exitCode = new CommandLine(new Server()).execute(args);
        System.exit(exitCode);
    }
}