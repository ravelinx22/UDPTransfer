package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import Utils.Constants;
import Utils.Segment;

public class UDPClient {
	/* Attributes */
	private DatagramSocket socket;
	private int port;
	private InetAddress address;
	private int chunkSize;
	private int segmentSize;

	/* Constructor */
	public UDPClient(String address, int port, int bufferSize) throws Exception {
		this.port = port;
		this.address = InetAddress.getByName(address);
		this.socket = new DatagramSocket(7070);
		this.segmentSize = 1024*bufferSize;
		this.chunkSize = segmentSize-Constants.headerSize;
	}

	/* Methods */
	public void receiveFile() throws Exception {
		long tiempoInicial = System.currentTimeMillis();
		
		File file;
		RandomAccessFile out;
		int numChunks;
		long fileLength;

		byte[] buffer = new byte[this.segmentSize];
		DatagramPacket packet = new DatagramPacket(buffer, this.segmentSize);
		Segment segment;
		boolean[] received;
		int index;
		
		// Send client address so the server knows where to send the file.
		String reply = InetAddress.getLocalHost().getHostAddress();
		byte[] replyBytea = reply.getBytes();
		DatagramPacket replyPacket = new DatagramPacket(replyBytea, replyBytea.length, this.address, this.port);
		socket.send(replyPacket);

		// Receive file name and length
		while (true) {
			socket.receive(packet);
			segment = new Segment(packet, true);

			if (segment.isSegmentMetadata() && segment.isCorrect()) {
				fileLength = segment.getFileLength();
				numChunks = (int) Math.ceil(1F * fileLength / this.chunkSize);

				file = new File(segment.getFileName());
				file.getAbsoluteFile().getParentFile().mkdirs();
				out = new RandomAccessFile(file, "rw");

				sendAck(segment.getSequenceNumber());
				break;
			}
		}

		// Receive file
		received = new boolean[numChunks];
		while (numChunks > 0) {
			socket.receive(packet);
			segment = new Segment(packet, false);

			if (segment.isCorrect()) {
				if (!segment.isSegmentMetadata()) {
					index = segment.getSequenceNumber() - 1;
					if (!received[index]) {
						out.seek(index * this.chunkSize);
						out.write(segment.getData(), 0, segment.getDataLength());
						received[index] = true;
						numChunks--;
					}
				}
				sendAck(segment.getSequenceNumber());
			}
		}

		out.setLength(fileLength);

		out.close();
		
		long lastTime = (System.currentTimeMillis()-tiempoInicial)*1000;
		System.out.println("Tomo " + lastTime + " segundos");
	}

	// Sends an ack each time a package is received correctly
	private void sendAck(int sequenceNumber) throws Exception {
		Segment segment = new Segment(sequenceNumber, new byte[0]);
		DatagramPacket packet = new DatagramPacket(segment.getBytes(), segment.getLength(), this.address, this.port);
		socket.send(packet);
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
		
		UDPClient fl = new UDPClient(address, port, bufferSize);
		fl.receiveFile();
	}
}
