package no.ntnu.item.csv.fileutils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

	public static boolean filesMatch(String filepath1, String filepath2)
			throws IOException {
		File file1 = new File(filepath1);
		File file2 = new File(filepath2);

		byte[] content1 = FileUtils.readDataBinary(new FileInputStream(file1),
				(int) file1.length());
		byte[] content2 = FileUtils.readDataBinary(new FileInputStream(file2),
				(int) file2.length());

		if (content1.length != content2.length) {
			return false;
		}

		for (int i = 0; i < content2.length; i++) {
			if (content1[i] != content2[i]) {
				return false;
			}
		}
		return true;
	}
}
