package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
	/* Constants */
	public final static int PORT = 7070;
	public final static String ADDRESS = "localhost";
	
	/* Attributes */
	private DatagramSocket socket;
	private InetAddress serverAddress;
	private Scanner scanner;
	
	/* Constructors */
	public UDPClient(String serverAddress, int port) throws IOException {
		this.serverAddress = InetAddress.getByName(serverAddress);
		this.socket = new DatagramSocket(PORT);
		this.scanner = new Scanner(System.in);
	}
	
	/* Methods */
	private int start() throws IOException {
		System.out.println("Running client");
		String in;
		
		while(true) {
			in = scanner.nextLine();	
			DatagramPacket packet = new DatagramPacket(in.getBytes(), in.getBytes().length, this.serverAddress, PORT);
			this.socket.send(packet);
		}
	}
	
	/* Main */
	public static void main(String[] args) throws Exception {
		UDPClient client = new UDPClient(UDPClient.ADDRESS, UDPClient.PORT);
		client.start();
	}
}
