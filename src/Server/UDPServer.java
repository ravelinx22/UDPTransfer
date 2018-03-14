package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer {
	
	/* Constants */
	public final static int PORT = 7070;
	
	/* Attributes */
	private DatagramSocket socket;
	
	/* Constructors */
	public UDPServer() throws SocketException, IOException {
		this.socket = new DatagramSocket(PORT);
	}
	
	/* Methods */
	private void listen() throws Exception {
		System.out.println("Running server");
		String msg;
		
		while(true) {
			byte[] buffer = new byte[24*2014];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			socket.receive(packet);
			msg = new String(packet.getData()).trim();
			
			System.out.println("Message received from " + packet.getAddress().getHostAddress() + " was " + msg);
		}
	}
	
	/* Main */
	public static void main(String[] args) throws Exception {
		UDPServer server = new UDPServer();
		server.listen();
	}
}
