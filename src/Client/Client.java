package src.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Client class for connecting to Server through UDP sockets
 */
public class Client {
	// Socket to send packets from
	private DatagramSocket ds;

	// Ip and port of server to connect to
	private InetAddress ip;
	private int port;

	// Ip and port of client
	private String clientId;
	private int clientPort;

	// Req count to differentiate between messages
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

		// Store own IP and port
		this.clientId = InetAddress.getLocalHost().getHostAddress();
		this.clientPort = this.ds.getLocalPort();
		this.reqCount = 0;
	}

	/**
	 * Increments request id, and returns it
	 * 
	 * @return Request id
	 */
	public String getReqId() {
		this.reqCount += 1;
		return String.format("%s:%d|%d", this.clientId, this.clientPort, this.reqCount);
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
	 * Pauses client for a duration of time.
	 * Within this interval, client receives all packets from the server.
	 * @param duration Time in minutes to wait for
	 */
	public void receiveAll(int duration) {
		final Thread thisThread = Thread.currentThread();
		final int timeout = duration * 60000; // time in millis

		// Starts a timer to interrupt the current thread after a period of time
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(timeout);
					thisThread.interrupt();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		// Client receives all packets during this period of time
		while (!Thread.interrupted()) {
			try {
				System.out.println("[UPDATE]: " + receive());
			} catch (SocketTimeoutException e) {
				// No need to handle
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Monitoring has expired");
	}

	/**
	 * Sends a message to server, and waits for a response
	 * If no there is no response before the timeout, null string will be returned
	 * 
	 * @param msg     Message to be sent
	 * @param timeout Time to wait in milliseconds
	 * @param noResend Flag to not resend message
	 * @return Message from server
	 * @throws IOException
	 */
	public String sendWithTimeout(String msg, int timeout, boolean noResend) throws IOException {
		send(msg);
		this.ds.setSoTimeout(timeout);

		while (true) {
			try {
				return receive();
			} catch (SocketTimeoutException e) {
				if (!noResend) {
					// Resends packet if no response received within the time interval
					System.out.println("No response received, resending packet...");
					return sendWithTimeout(msg, timeout, noResend);
				}
				return null;
			}
		}
	}
}
