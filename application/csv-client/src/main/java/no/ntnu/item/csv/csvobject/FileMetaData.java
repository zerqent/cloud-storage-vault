package no.ntnu.item.csv.csvobject;

public class FileMetaData {

	private String filename;
	private String mimeType; // TODO;
	private int size;
//	private byte[] digest;
	
	public FileMetaData(String filename, String mimeType, int size) {
		this.filename = filename;
		this.mimeType = mimeType;
		this.size = size;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	public int getsize() {
		return this.size;
	}
	
}
