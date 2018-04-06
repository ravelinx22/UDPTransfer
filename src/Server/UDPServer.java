package Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Scanner;

import Utils.Constants;
import Utils.AckReceiverThread;
import Utils.Segment;

public class UDPServer {

	/* Attributes*/
	private DatagramSocket socket;
	private String file_path;
	private String save_path;
	private int segmentSize;
	private int chunkSize;
	
	/* Constructor*/
	public UDPServer(int port, String src, String dest, int bufferSize) throws Exception {
		this.file_path = src;
		this.save_path = dest;
		this.segmentSize = 1024*bufferSize;
		this.chunkSize = this.segmentSize-Constants.headerSize;
		socket = new DatagramSocket(port);
	}

	public void receiveRequest() throws Exception  {
		while(true){
			long tiempoInicial = System.currentTimeMillis();
			// Get client address and send file
			byte[] incomingData = new byte[24*1024];
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
			socket.receive(incomingPacket);
			String request =  new String(incomingPacket.getData());
			sendFile(new InetSocketAddress(request, 7070));
			
			long lastTime = (System.currentTimeMillis()-tiempoInicial)/1000;
			System.out.println("Tomo " + lastTime + " segundos");
		}
	}

	/* Methods */
	public void sendFile(InetSocketAddress clientAdress) throws Exception {
		DatagramPacket packet;

		RandomAccessFile in = new RandomAccessFile(new File(file_path), "r");

		int numChunks = (int) Math.ceil(1F * in.length() / this.chunkSize);

		boolean[] received = new boolean[numChunks + 1];
		AckReceiverThread ackReceiverThread = (new AckReceiverThread(received, numChunks, socket));
		ackReceiverThread.start();

		boolean completed = false;
		Segment segment;
		byte[] buffer = new byte[this.chunkSize];

		// Send file name and length
		while (!received[0]) {
			segment = getMetadata(in.length(), save_path);
			packet = new DatagramPacket(segment.getBytes(), segment.getLength(), clientAdress);
			socket.send(packet);
		}

		// Send file content
		while (!completed) {
			completed = true;
			for (int i = 1; i <= numChunks; i++) {
				if (received[i]) {
					continue;
				}
				completed = false;
				in.seek((i - 1) * this.chunkSize);
				in.read(buffer, 0, this.chunkSize);
				segment = new Segment(i, buffer);
				packet = new DatagramPacket(segment.getBytes(), segment.getLength(), clientAdress);
				socket.send(packet);
			}
		}
		in.close();
	}

	// Gets the file name and length
	private Segment getMetadata(long fileLength, String fileName) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(this.chunkSize);
		buffer.putLong(fileLength);
		buffer.putInt(fileName.length());
		buffer.put(fileName.getBytes());
		return new Segment(0, buffer.array());
	}

	/* Main */
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Ingrese el tamaño del buffer: ");
		int bufferSize = sc.nextInt();
		sc.nextLine();
		System.out.println("Ingrese la ruta del archivo que va a ofrecer: ");
		String file_path = sc.nextLine();
		System.out.println("Ingrese la ruta de como va a almacenar el archivo: ");
		String save_path = sc.nextLine();
		sc.close();
		
		UDPServer fileSender = new UDPServer(Integer.parseInt(args[0]), file_path, save_path, bufferSize);
		fileSender.receiveRequest();
	}
}
