package Client;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Scanner;

import Utils.FileEvent;

public class UDPClient {
	/* Constants */
	public final static String SEPARATOR = ";";
	public final static String HASH_ALGORITHM = "MD5";
	public final static String SEND_FILE = "SEND_FILE";
	
	/* Attributes */
	private DatagramSocket socket;
	private InetAddress serverAddress;
	private int bufferSize;
	
	/* Constructors */
	public UDPClient(String serverAddress, int port, int bufferSize) throws IOException {
		this.socket = new DatagramSocket(port);
		this.serverAddress = InetAddress.getByName(serverAddress);
		this.bufferSize = bufferSize;
	}

	/* Methods */
	public void createConnection() throws Exception {
		byte[] incomingData = new byte[this.bufferSize*1024];
		long initTime = System.currentTimeMillis();
		sendRequest(serverAddress, 7070);
		System.out.println("File request send");
		
		while (true) {
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			System.out.println("Package received");
			FileEvent fileEvent = readFile(incomingPacket);
			createAndWriteFile(fileEvent);
			long totalTime = System.currentTimeMillis() - initTime;
			
			compareFileHashes(fileEvent);
			System.out.println("File saved - Took " + totalTime + " ms");
			break;
		}	
	}

	public FileEvent readFile(DatagramPacket incomingPacket) throws Exception {
		socket.receive(incomingPacket);
		byte[] data = incomingPacket.getData();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return (FileEvent) is.readObject();
	}

	public void createAndWriteFile(FileEvent fileEvent) throws Exception {
		String outputFile = "./" + fileEvent.getFilename();
		File dstFile = new File(outputFile);
		FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
		fileOutputStream.write(fileEvent.getFileData());
		fileOutputStream.flush();
		fileOutputStream.close();
	}
	
	public void sendRequest(InetAddress address, int port) throws Exception {
		String reply = SEND_FILE + SEPARATOR + InetAddress.getLocalHost().getHostAddress();
		
		byte[] replyBytea = reply.getBytes();
		DatagramPacket replyPacket = new DatagramPacket(replyBytea, replyBytea.length, address, port);
		socket.send(replyPacket);
	}
	
	public void compareFileHashes(FileEvent fileEvent) throws Exception {
		String receivedHash = new String(fileEvent.getMd5Hash());
		byte[] calculatedHash = MessageDigest.getInstance(HASH_ALGORITHM).digest(fileEvent.getFileData());
		String localHash = new String(calculatedHash);
		
		if(receivedHash.equals(localHash)) {
			System.out.println("File received without errors");
		} else {
			System.out.println("File received with errors");
		}
	}

	/* Main */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Escoger dirección IP del servidor:");
		String address = sc.nextLine();
		System.out.println("Escoger puerto:");
		int port = sc.nextInt();
		System.out.println("Ingrese el tamaño del buffer: ");
		int bufferSize = sc.nextInt();
		sc.close();
		
		UDPClient client = new UDPClient(address, port, bufferSize);
		client.createConnection();
	}
}
