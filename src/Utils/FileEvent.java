package Utils;

import java.io.Serializable;

public class FileEvent implements Serializable {
	/* Serializable */
	private static final long serialVersionUID = 1L;

	/* Attributes */
	private long fileSize;
	private byte[] fileData;
	private String filename;
	private byte[] md5Hash;

	/* Getters */
	public String getFilename() {
		return this.filename;
	}
	
	public long getFileSize() {
		return this.fileSize;
	}
	
	public byte[] getFileData() {
		return this.fileData;
	}
	
	public byte[] getMd5Hash() {
		return this.md5Hash;
	}
	
	/* Setters */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	
	public void setMd5Hash(byte[] md5Hash) {
		this.md5Hash = md5Hash;
	}
}