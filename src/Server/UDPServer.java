package Server;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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
		String msg;
		
		try {
			while(true) {
				byte[] buffer = new byte[24*2014];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				PrintWriter pw = new PrintWriter(new FileWriter("./data/" +(packet.getAddress().getHostAddress()) + ".txt", true));				
				msg = new String(packet.getData()).trim();
				String[] data = msg.split(SEPARATOR);
				int id = Integer.parseInt(data[1]);
				pw.write(msg +"\n");
				pw.close();
				
				Long timestamp = System.currentTimeMillis() - Long.parseLong(data[2]);
				System.out.println(id + ": " + timestamp + " ms");
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
