package no.ntnu.item.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.activation.MimetypesFileTypeMap;

public class FileContainer {
	
	private String contentType;
	private long contentLength;
	private Byte[] data;
	private String encoding;
//	private String name; // TODO
//	private String path; // TODO
	
	public FileContainer(byte[] data, String contentType) {
		this.contentType = contentType;
		this.setData(data);
	}
	
	public FileContainer(Byte[] data, String contentType) {
		this.data = data;
		this.contentType = contentType;
		this.contentLength = this.data.length;
	}
	
	public FileContainer(String contentType) {
		this.contentType = contentType;
	}
	
	private FileContainer() {
		
	}
	
	public void setData(byte[] data) {
		this.data = new Byte[data.length];
		for (int i = 0; i < data.length; i++) {
			this.data[i] = new Byte(data[i]);
		}
		this.contentLength = data.length;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public Byte[] getData() {
		return data;
	}

	public void setData(Byte[] data) {
		this.data = data;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String toString() {
		String s = "";
		for (int i = 0; i < this.data.length; i++) {
			s += this.data[i].toString();
		}
		return s;
	}
	
	public byte[] getPrimitiveByte() {
		byte[] b = new byte[this.data.length];
		for (int i = 0; i < this.data.length; i++) {
			b[i] = this.data[i].byteValue();
		}
		return b;
	}
	
	public String hexDigest() {
		return FileContainer.hexDigest(this.getPrimitiveByte());
	}
	
	public void readDataBinary(InputStream in, int filelength) throws IOException {
		//TODO: 32-bit warning right here..
		byte[] bytes = new byte[filelength];
		int offset = 0;
		int numRead = 0;
		try {
			while (offset < bytes.length && (numRead=in.read(bytes, offset, bytes.length-offset)) >= 0) {
			        offset += numRead;
			    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (offset < bytes.length) {
			throw new IOException("Could not read entire file");
		}
		this.contentLength = filelength;
		this.setData(bytes);
	}
	
	public static FileContainer fromFile(File file) throws IOException {	
		InputStream in = new FileInputStream(file);
		FileContainer container = new FileContainer();
		container.contentType = new MimetypesFileTypeMap().getContentType(file);
		container.readDataBinary(in, (int)file.length());
		return container;
	}
	
	private static String hexDigest(byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(bytes);
			byte[] hash = md.digest();
			return convertToHex(hash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			// Should not Happen
			e.printStackTrace();
			return null;
		}
	}
	
	// FROM: http://www.anyexample.com/programming/java/java_simple_class_to_compute_md5_hash.xml
	private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 

}


