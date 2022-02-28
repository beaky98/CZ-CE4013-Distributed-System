package src.Client;

//TO BE MERGED WITH UDPCLIENT?


// Java program to illustrate Client side
// Implementation using DatagramSocket
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Client {
	private DatagramSocket ds;
	private InetAddress ip;
	private int port;
	private String clientId;
	private int reqCount;

	/**
	 * Constructor of Client class
	 * Each client is connected to a single server
	 * 
	 * @param ip   Server ip address
	 * @param port Server port number
	 * @throws IOException
	 */
	public Client(String ip, int port) throws IOException {
		// Create socket
		this.ds = new DatagramSocket();

		// Save server IP and port
		this.ip = InetAddress.getByName(ip);
		this.port = port;

		// this.ds.connect(this.ip, this.port);

		this.clientId = InetAddress.getLocalHost().getHostAddress();
		this.reqCount = 0;
	}

	/**
	 * Increments request id, and returns it
	 * 
	 * @return Request id
	 */
	public String getReqId() {
		this.reqCount += 1;
		return this.clientId + "|" + this.reqCount;
	}

	/**
	 * Sends a message to server
	 * 
	 * @param msg Message to be sent
	 * @throws IOException
	 */
	public void send(String msg) throws IOException {
		byte[] buf = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, this.ip, this.port);
		this.ds.send(packet);
	}

	/**
	 * Receives message from server
	 * 
	 * @return Message from server
	 * @throws IOException
	 */
	public String receive() throws IOException {
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		this.ds.receive(packet);

		String received = new String(packet.getData(), 0, packet.getLength());
		return received;
	}

	/**
	 * Sends a message to server, and waits for a response
	 * If no there is no response before the timeout, null string will be returned
	 * 
	 * @param msg     Message to be sent
	 * @param timeout Time to wait in milliseconds
	 * @param resend Flag to keep resending message until success
	 * @return Message from server
	 * @throws IOException
	 */
	public String sendWithTimeout(String msg, int timeout, boolean resend) throws IOException {
		send(msg);
		this.ds.setSoTimeout(timeout);

		while (true) {
			try {
				return receive();
			} catch (SocketTimeoutException e) {
				if (resend) {
					System.out.println("No response received, resending packet...");
					return sendWithTimeout(msg, timeout, resend);
				}
				return null;
			}
		}
	}
}
