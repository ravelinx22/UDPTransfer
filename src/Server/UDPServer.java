package Server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

import Utils.Message;

public class UDPServer {
	
	/* Constants */
	public final static String SEPARATOR = ";";
	
	/* Attributes */
	private DatagramSocket socket;
	
	/* Constructors */
	public UDPServer(int port) throws SocketException, IOException {
		this.socket = new DatagramSocket(port);
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
				
				String url  = "./" +(packet.getAddress().getHostAddress()) + ".txt";
				File file = new File(url);
				if(mssg.getId() == 1 && file.exists()) {
					file.delete();
					file.createNewFile();
				} else if(!file.exists()) {
					file.createNewFile();
				}
				
				String metaData = calculateMetaData(file, mssg);	
				PrintWriter pw = new PrintWriter(new FileWriter(url, true));
				pw.println(mssg.toString());
				pw.println(metaData);
				pw.close();
				System.out.println(mssg.toString());
				System.out.println(metaData);
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// LOST;RECEIVED;AVERAGE
	private String calculateMetaData(File file, Message mssg) throws Exception {
		Scanner sc = new Scanner(new FileReader(file));
		String previousLine = null;
		String lastLine = null;
		
		while(sc.hasNextLine()) {
			previousLine = lastLine;
			lastLine = sc.nextLine();
		}
		
		int lostPackets = 0;
		int receivedPackets = 0;
		double averageTime = 0.0;
		int idLastMessage = 1;

		if(previousLine != null && lastLine != null) {
			String[] data = lastLine.split(SEPARATOR);
			lostPackets = Integer.parseInt(data[0]);
			receivedPackets = Integer.parseInt(data[1]);
			averageTime = Double.parseDouble(data[2]);
			String[] lastData = previousLine.split(":");
			idLastMessage = Integer.parseInt(lastData[0]);
		}
		
		if((mssg.getId()-idLastMessage) > 1) {
			lostPackets += (mssg.getId()-idLastMessage)-1;
		}
		receivedPackets++;
		averageTime = (averageTime+mssg.getTravelingTime())/2.0;
		
		String metaData = lostPackets + SEPARATOR + receivedPackets + SEPARATOR + averageTime;
		sc.close();
		return metaData;
		
	}
	
	/* Main */
	public static void main(String[] args) throws Exception {
		UDPServer server = new UDPServer(Integer.parseInt(args[0]));
		server.listen();
	}
}
