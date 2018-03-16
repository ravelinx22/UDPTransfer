package Server;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Utils.Message;

public class UDPServer {
	
	/* Constants */
	public final static int PORT = 7070;
	public final static String SEPARATOR = ";";
	
	/* Attributes */
	private DatagramSocket socket;
	
	/* Constructors */
	public UDPServer() throws SocketException, IOException {
		this.socket = new DatagramSocket(PORT);
	}
	
	/* Methods */
	private void listen() throws Exception {
		System.out.println("Running server");
		
		try {
			while(true) {
				byte[] buffer = new byte[24*2014];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				byte[] data = packet.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				Message mssg = (Message) is.readObject();
				mssg.markAsReceived();
				
				PrintWriter pw = new PrintWriter(new FileWriter("./data/" +(packet.getAddress().getHostAddress()) + ".txt", true));				
				pw.write(mssg.toString() +"\n");
				pw.close();
				System.out.println(mssg.toString());
			}	

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/* Main */
	public static void main(String[] args) throws Exception {
		UDPServer server = new UDPServer();
		server.listen();
	}
}
