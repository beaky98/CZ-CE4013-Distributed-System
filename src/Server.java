package src;
// Java program to illustrate Server side
// Implementation using DatagramSocket
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server
{
	private static DatagramSocket ds;
	private static int server_port;
	private static double loss_rate;

	public static void main(String[] args) throws IOException
	{
		// Create socket
		server_port = 1234;
		ds = new DatagramSocket(server_port);

		// Percentage of messages to be dropped
		loss_rate = 0.2;

		System.out.printf("Running server on port %s with loss rate of %.3f...\n", server_port, loss_rate);

		while (true)
		{
			// Create and receive packet
			DatagramPacket packet = receive();

			String received = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Client: " + received);

			// Get client address and port
			InetAddress ip = packet.getAddress();
            int port = packet.getPort();
			
			String msg = "Server received: " + received;
			if (Math.random() > loss_rate)
			{
				send(msg, ip, port);
			}

			// Exit the server if the client sends "bye"
			if (received.equals("bye"))
			{
				System.out.println("Client sent bye.....EXITING");
				break;
			}
		}
        ds.close();
	}

	/**
	 * Sends a message to client
	 * 
	 * @param msg Message to be sent
	 * @param ip IP address of client
	 * @param port Port number of client
	 * @throws IOException
	 */
	private static void send(String msg, InetAddress ip, int port) throws IOException
	{
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
	private static DatagramPacket receive() throws IOException
	{
		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		ds.receive(packet);

		return packet;
	}

	
}