package src.Server;

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
	int server_port = 2222;

    @Option(names = "--loss", description = "percentage of packet loss")
	double loss_rate = 0.2;
	
    @Option(names = "--once", description = "at-least-once invocation semantic")
	boolean at_least_once = false;
	
	private DatagramSocket ds;
	private HashMap<String, String> reqList;

	private Services bankService;

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

			String req = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Client: " + req);

			//Unmarshal request to get request id
			String[] reqArr = req.split("_");
			// for (int i=0; i<reqArr.length; i++)
			// 	System.out.println(reqArr[i]);
			String reqId = reqArr[0];

			// Checks if request has been sent before
			String res = checkReqId(reqId);

			// Get client address and port
			InetAddress ip = packet.getAddress();
			int port = packet.getPort();

			// Exit the server if the client sends "bye"
			if (reqArr[1].equals("-1")) {
				System.out.println("Client sent bye.....EXITING");
				break;
			}

			// New request
			if (res == null) {

				String response = "";
				switch (Integer.parseInt(reqArr[1])) { 				// Unmarshal request to get request type
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
						response = bankService.monitorUpdate(ip.getHostAddress(), port, Integer.parseInt(reqArr[2])); //this one not done
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


				// TODO: Banking Service perfoms request and returns response. KK: NO NEED HOR?
				// TODO: Marshal response. KK: NO NEED HOR?

				res = response;
				//res = "Got message: " + reqArr[1];

				// Stores response
				storeRes(reqId, res);
			}

			if (Math.random() > loss_rate) {
				send(res, ip, port);
			}
			else {
				System.out.println("Dropping packet...");
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
	
	public class CallbackHandler implements Callback {
		public void sendMessage(String msg, String ip, int port) throws IOException {
			send(msg, InetAddress.getByName(ip), port);
		}
	}

	public static void main(String[] args) {
        int exitCode = new CommandLine(new Server()).execute(args);
        System.exit(exitCode);
    }
}