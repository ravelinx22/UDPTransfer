package Utils;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AckReceiverThread extends Thread {
	private final boolean[] isSegmentReceived;
	private int chunks;
	private DatagramSocket socket;

	public AckReceiverThread(boolean[] received, int numChunks, DatagramSocket socket) {
		this.isSegmentReceived = received;
		this.chunks = numChunks;
		this.socket = socket;
	}

	// Receives Ack to check if segment was received.
	public void run() {
		byte[] buffer = new byte[Constants.headerSize];
		Segment segment;
		DatagramPacket packet = new DatagramPacket(buffer, Constants.headerSize);
		while (chunks >= 0) {
			// Receive packet
			try {
				socket.receive(packet);
				segment = new Segment(packet, false);
				
				// If was received correctly then mark as received
				if (segment.isCorrect() && segment.getSequenceNumber() == -1) {
					for (int i = 0; i < isSegmentReceived.length; i++) {
						isSegmentReceived[i] = true;
					}
					return;
				}
				
				// Decrease the number of chunks that hasnt being received.
				if (segment.isCorrect() && !isSegmentReceived[segment.getSequenceNumber()]) {
					isSegmentReceived[segment.getSequenceNumber()] = true;
					chunks--;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
