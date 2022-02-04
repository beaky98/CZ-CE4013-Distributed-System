package src;

// Java program to illustrate Server side
// Implementation using DatagramSocket
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {
	private static DatagramSocket ds;
	private static int server_port;
	private static double loss_rate;
	private static boolean at_most_once;
	private static HashMap<String, String> reqList;

	public static void main(String[] args) throws IOException {
		// Create socket
		server_port = 1234;
		ds = new DatagramSocket(server_port);

		// Percentage of messages to be dropped
		loss_rate = 0.2;

		// Invocation semantics
		at_most_once = false;
		reqList = new HashMap<String, String>();

		System.out.printf("Running server on port %s with loss rate of %.3f...\n", server_port, loss_rate);

		while (true) {
			// Create and receive packet
			DatagramPacket packet = receive();

			String req = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Client: " + req);

			// Get client address and port
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();

			// TODO: Unmarshal request to get request id
			String reqId = "reqId";

			// Checks if request has been sent before
			String res = checkReqId(reqId);

			// New request
			if (res == null) {
				// TODO: Unmarshal request to get request type
				// TODO: Banking Service perfoms request and returns response
				// TODO: Marshal response
				res = req;

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
	}

	/**
	 * Sends a message to client
	 * 
	 * @param msg  Message to be sent
	 * @param ip   IP address of client
	 * @param port Port number of client
	 * @throws IOException
	 */
	private static void send(String msg, InetAddress ip, int port) throws IOException {
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
	private static DatagramPacket receive() throws IOException {
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
	private static String checkReqId(String reqId) {
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
	private static void storeRes(String reqId, String response) {
		if (at_most_once)
			reqList.put(reqId, response);
	}
}