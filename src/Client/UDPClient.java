package Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import Utils.Message;

public class UDPClient {
	/* Constants */
	public final static int PORT = 7070;
	public final static String ADDRESS = "52.203.207.37";
	public final static String SEPARATOR = ";";
	
	/* Attributes */
	private DatagramSocket socket;
	private InetAddress serverAddress;
	
	/* Constructors */
	public UDPClient(String serverAddress, int port) throws IOException {
		this.serverAddress = InetAddress.getByName(serverAddress);
		this.socket = new DatagramSocket(PORT);
	}
	
	/* Methods */
	private void sendMessages(int messages) throws IOException {
		System.out.println("Running client");
		
		while(messages > 0) {	
			Message student = new Message("buenas");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(student);
			byte[] data = outputStream.toByteArray();
			
			DatagramPacket packet = new DatagramPacket(data, data.length, this.serverAddress, PORT);
			this.socket.send(packet);
			messages--;
		}
	}
	
	/* Main */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escoger dirección IP del servidor:");
		String address = sc.nextLine();
		System.out.println("Escoger puerto:");
		int port = sc.nextInt();
		System.out.println("Escoger numero de mensajes a enviar:");
		int messages = sc.nextInt();
		sc.close();
		
		UDPClient client = new UDPClient(address, port);
		client.sendMessages(messages);
	}
}
