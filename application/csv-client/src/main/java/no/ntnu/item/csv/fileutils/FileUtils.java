package no.ntnu.item.csv.fileutils;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

	public static void writeFileToDisk(String path, byte[] data) {
		try {
			DataOutputStream os = new DataOutputStream(new FileOutputStream(
					path));
			os.write(data);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] readDataBinary(InputStream in, int filelength)
			throws IOException {
		// TODO: 32-bit warning right here..
		byte[] bytes = new byte[filelength];
		int offset = 0;
		int numRead = 0;

		while (offset < bytes.length
				&& (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not read entire file");
		}
		return bytes;
	}

	public static String getFileExtension(String result) {
		if (result == null) {
			return null;
		}

		int nDots = result.lastIndexOf(".");
		if (nDots >= 0) {
			return result.substring(nDots + 1);
		} else {
			return "";
		}
	}
}
