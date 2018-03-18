package Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.Scanner;

import Utils.FileEvent;

public class UDPServer {
	/* Constants */
	public final static String SEPARATOR = ";";
	public final static String HASH_ALGORITHM = "MD5";
	public final static String SEND_FILE = "SEND_FILE";
	public final static int CLIENT_PORT = 7070;
	
	/* Attributes */
	private DatagramSocket socket;
	private int bufferSize;
	private String file_path;

	/* Constructors */
	public UDPServer(int port, int bufferSize, String file_path) throws Exception {
		this.socket = new DatagramSocket(port);
		this.bufferSize = bufferSize;
		this.file_path = file_path;
	}

	/* Methods */
	public void createAndListenSocket() throws Exception{
		System.out.println("Running server");
		while(true) {
			String request = receiveRequest().trim();	
			System.out.println("User received");
			if(request.contains(SEND_FILE)) {
				System.out.println("File request received");
				String address = getIpAdress(request);
				FileEvent event = getFileEvent();
				sendFile(event, address, CLIENT_PORT);
				System.out.println("File send");
			}
		}	
	}
	
	public void sendFile(FileEvent event, String address, int port) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(outputStream);
		os.writeObject(event);
		byte[] data = outputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(address), port);
		socket.send(sendPacket);
		System.out.println("File sent from client");
	}



	public String receiveRequest() throws Exception {
		byte[] incomingData = new byte[this.bufferSize*1024];
		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		socket.receive(incomingPacket);
		return new String(incomingPacket.getData());
	}

	/* Helpers */
	public FileEvent getFileEvent() throws Exception {
		FileEvent fileEvent = new FileEvent();
		fileEvent.setFilename(getFileName(file_path));
		File file = new File(file_path);

		if(!file.isFile())
			throw new Exception("Invalid path");

		readFile(file, fileEvent);
		
		return fileEvent;
	}

	public void readFile(File file, FileEvent fileEvent) throws Exception {
		DataInputStream diStream = new DataInputStream(new FileInputStream(file));
		long len = (int) file.length();
		byte[] fileBytes = new byte[(int) len];

		int read = 0;
		int numRead = 0;
		while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
			read = read + numRead;
		}

		fileEvent.setFileSize(len);
		fileEvent.setFileData(fileBytes);
		fileEvent.setMd5Hash(MessageDigest.getInstance(HASH_ALGORITHM).digest(fileEvent.getFileData()));
		diStream.close();
	}

	public String getFileName(String filePath) {
		return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
	}
	
	public String getIpAdress(String request) throws Exception {
		String[] data = request.split(SEPARATOR);
		return data[1];
	}

	/* Main */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Ingrese el tamaño del buffer: ");
		int bufferSize = sc.nextInt();
		sc.nextLine();
		System.out.println("Ingrese la ruta del archivo que va a ofrecer: ");
		String file_path = sc.nextLine();
		sc.close();
		
		UDPServer server = new UDPServer(Integer.parseInt(args[0]), bufferSize, file_path);
		server.createAndListenSocket();
	}
}
