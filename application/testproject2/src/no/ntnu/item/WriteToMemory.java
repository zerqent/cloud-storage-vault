package no.ntnu.item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.MemoryFile;

public class WriteToMemory extends Activity {

	public MemoryFile mem;
	public byte[] bytearray = new byte[13];
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		try {
			URL url = new URL("https://s3-eu-west-1.amazonaws.com/tileivind/staticfile.txt");
			InputStream is = url.openStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			while ((nRead = is.read(bytearray, 0, bytearray.length)) != -1) {
			  buffer.write(bytearray, 0, nRead);
			}
			buffer.flush();
			mem = new MemoryFile("mem", 13);
			mem.writeBytes(bytearray, 0, 0, bytearray.length);
			
			System.out.println("Memory file is created!");
			//put: Intent      putExtra(String name, byte[] value) 
			//get: byte[]      getByteArrayExtra(String name) 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
