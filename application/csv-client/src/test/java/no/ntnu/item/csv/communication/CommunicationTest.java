package no.ntnu.item.csv.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CommunicationTest {

	private Communication com = CommunicationFactory
			.createCommunicationFromProperties();

	private final String testfile = "src/test/resources/smallloremipsum.txt";
	private final String outputFile = "/tmp/output.txt";

	private File myFile = new File(this.testfile);
	private File myOutputFile = new File(this.outputFile);

	@Before
	public void setUp() throws IOException {
		Assert.assertFalse("You must set the user/password", this.com
				.getPassword().equals("") || this.com.getUsername().equals(""));
	}

	@After
	public void tearDown() {
		if (myOutputFile.exists()) {
			myOutputFile.delete();
		}
	}

	@Test
	public void testtestLogin() throws ClientProtocolException, IOException {
		Assert.assertTrue(com.testLogin());
	}

	private String getARandomUrl() {
		CSVFolder folder = new CSVFolder();
		return folder.getCapability().getStorageIndex();
	}

	@Test
	public void testPutAndGetInputStream() throws IOException {
		FileInputStream is = new FileInputStream(this.myFile);
		String index = getARandomUrl();

		int result = this.com.putInputStream(index, is, this.myFile.length());
		Assert.assertEquals(201, result);

		FileOutputStream fos = new FileOutputStream(this.myOutputFile);

		HttpResponse response = this.com.get(index);

		Assert.assertEquals(200, response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			entity.writeTo(fos);
			fos.flush();
			fos.close();
		}

		Assert.assertTrue(this.myOutputFile.exists());
		byte[] org = FileUtils.readDataBinary(new FileInputStream(this.myFile),
				(int) this.myFile.length());
		byte[] downloaded = FileUtils.readDataBinary(new FileInputStream(
				this.myOutputFile), (int) this.myOutputFile.length());

		Assert.assertArrayEquals(org, downloaded);

	}

	@Test
	public void testPutAndGetByteArray() throws IllegalStateException,
			IOException {
		byte[] org = new String(
				"This is a byte array, well right now its a string, but look ahead and you will see .getBytes()")
				.getBytes();

		String index = this.getARandomUrl();
		int result = this.com.putByteArray(index, org);
		Assert.assertEquals(201, result);

		HttpResponse response = this.com.get(index);

		Assert.assertEquals(200, response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();

		InputStream is;
		is = entity.getContent();

		int len = Integer.parseInt(response.getFirstHeader("Content-Length")
				.getValue());
		byte[] bytes = new byte[len];

		int nb;
		for (int i = 0; (nb = is.read()) != -1; i++) {
			bytes[i] = (byte) nb;
		}
		Assert.assertArrayEquals(org, bytes);

	}
}
