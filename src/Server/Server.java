package src.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.*;

/**
 * Server class for Clients to connect to through UDP sockets
 */
@Command(name = "server", description = "Starts the server for this app.", mixinStandardHelpOptions = true)
public class Server implements Callable<Integer> {

	// Additional flags for CLI
    @Option(names = "--port", description = "Port number to host the server on.")
	int server_port = 2222;

    @Option(names = "--loss", description = "Simulated rate of packet loss.")
	double loss_rate = 0.2;
	
    @Option(names = "--atleastonce", description = "Flag to use at-least-once invocation semantic.")
	boolean at_least_once = false;
	
	// Socket to send and receive packets
	private DatagramSocket ds;

	// HashMap to keep track of requestIDs and their responses
	private HashMap<String, String> reqList;

	// Services 
	private Services bankService;

	/**
	 * Main class
	 */
	public Integer call() throws Exception {
		// Create socket
		ds = new DatagramSocket(server_port);

		// Store request id / response pairs
		reqList = new HashMap<String, String>();

		// Bank service
		CallbackHandler handler = new CallbackHandler();
		bankService = new Services(handler);

		System.out.printf("Running server on port %s with loss rate of %.3f...\n", server_port, loss_rate);
		System.out.printf("Using invocation semantic: %s...\n", at_least_once ? "at-least-once": "at-most-once");

		while (true) {
			// Create and receive packet
			DatagramPacket packet = receive();

			// Get client address and port
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();

			String req = new String(packet.getData(), 0, packet.getLength());
			System.out.printf("[Request] %s\n", req);

			//Unmarshal request to get request id
			String[] reqArr = req.split("_");
			String reqId = reqArr[0];

			// Checks if request has been sent before
			String res = checkReqId(reqId);

			// Process new request
			if (res == null) {
				String response = "";

				// Unmarshal request to get request type
				switch (Integer.parseInt(reqArr[1])) {
					case 1:
						response = bankService.createAccount(reqArr[2], reqArr[3], reqArr[4], Double.parseDouble(reqArr[5]));
						break;
					case 2:
						response = bankService.closeAccount(reqArr[2], Integer.parseInt(reqArr[3]), reqArr[4]);
						break;
					case 3:
						response= bankService.updateBalance(reqArr[2], Integer.parseInt(reqArr[3]), reqArr[4], 0, reqArr[5], Double.parseDouble(reqArr[6]));
						break;
					case 4:
						response = bankService.updateBalance(reqArr[2], Integer.parseInt(reqArr[3]), reqArr[4], 1, reqArr[5], Double.parseDouble(reqArr[6]));
						break;
					case 5:
						response = bankService.monitorUpdate(ip.getHostAddress(), port, Integer.parseInt(reqArr[2]));
						break;
					case 6:
						response = bankService.checkBalance(reqArr[2], Integer.parseInt(reqArr[3]), reqArr[4]);  
						break;
					case 7:
						response = bankService.transferBalance(reqArr[2], Integer.parseInt(reqArr[3]), reqArr[4], Double.parseDouble(reqArr[5]), Integer.parseInt(reqArr[6]));
						break;
					default:
						response = "Option not found";
				}

				res = response;

				// Stores response
				storeRes(reqId, res);
			}
			System.out.printf("[Response] %s\n", res);

			// Simulate chance to drop the message
			if (Math.random() > loss_rate) {
				send(res, ip, port);
			}
			else {
				System.out.println("Dropping packet...");
			}


		}
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
		if (!at_least_once && reqList.containsKey(reqId)) {
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
		if (!at_least_once)
			reqList.put(reqId, response);
	}
	
	/**
	 * Implementation of callback
	 */
	public class CallbackHandler implements Callback {
		/**
		 * Sends a message to the ip and port
		 * @param msg Message to be sent
		 * @param ip Ip address of client
		 * @param port Port of client
		 * @throws IOException
		 */
		public void sendMessage(String msg, String ip, int port) throws IOException {
			send(msg, InetAddress.getByName(ip), port);
		}
	}

	/**
	 * Calls main function with CLI arguments
	 * @param args Additional CLI arguments
	 */
	public static void main(String[] args) {
        int exitCode = new CommandLine(new Server()).execute(args);
        System.exit(exitCode);
    }
}