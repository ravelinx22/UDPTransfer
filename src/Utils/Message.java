package Utils;
import java.io.Serializable;

public class Message implements Serializable {

	/* Serialization */
	private static final long serialVersionUID = 1L;
	
	/* Attributes */
	private Integer id;
	private String message;
	private Long startingTimestamp;
	private Long endingTimestamp;
	
	public Message(Integer id, String message) {
		this.id = id;
		this.startingTimestamp = System.currentTimeMillis();
	}
	
	/* Methods*/
	
	/**
	 * Saves the time at the moment of receiving the message.
	 * @return Returns the time taken from client to server.
	 */
	public Long markAsReceived() {
		this.endingTimestamp = System.currentTimeMillis();
		return this.getTravelingTime();
	}
	
	/**
	 * Calculates the time taken from the client to server.
	 * @return Returns the time taken from client to server.
	 */
	public Long getTravelingTime() {
		return this.endingTimestamp - this.startingTimestamp;
	}
	
	@Override
	public String toString() {
		return (this.endingTimestamp != null) ? this.id + ": " + this.getTravelingTime() : "Message havent being received";
	}
	
	/* Getters */
	public String getMessage() {
		return this.message;
	}
	
	public Long getStartingTimestamp() {
		return this.startingTimestamp;
	}
	
	public Integer getId() {
		return this.id;
	}
}
