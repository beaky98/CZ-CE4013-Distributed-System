package src;
// Java program to illustrate Client side
// Implementation using DatagramSocket
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client
{		
	private DatagramSocket ds;
	private InetAddress ip;
	private int port;

	/**
	 * Constructor of Client class
	 * Each client is connected to a single server
	 * 
	 * @param ip Server ip address
	 * @param port Server port number
	 * @throws IOException
	 */
	public Client(String ip, int port) throws IOException
	{
		// Create socket
		this.ds = new DatagramSocket();

		// Save server IP and port
		this.ip = InetAddress.getByName(ip);
		this.port = port;
	}

	/**
	 * Sends a message to server
	 * 
	 * @param msg Message to be sent
	 * @throws IOException
	 */
	public void send(String msg) throws IOException
	{
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
	public String receive() throws IOException
	{
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		this.ds.receive(packet);

		String received = new String(packet.getData(), 0, packet.getLength());
		return received;
	}
}