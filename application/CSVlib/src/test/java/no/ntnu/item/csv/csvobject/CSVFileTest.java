package no.ntnu.item.csv.csvobject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileTest {

	private CSVFile file;
	private final String testfile = "src/test/resources/smallloremipsum.txt";
	private final String encryptedOutputFile = "/tmp/enctest.txt";
	private final String decryptedOutputFile = "/tmp/dectest.txt";

	@Before
	public void setUp() throws IOException {
		this.file = new CSVFile(new File(this.testfile));
	}

	@After
	public void tearDown() {
		File f = new File(encryptedOutputFile);
		File f2 = new File(decryptedOutputFile);
		if (f.exists()) {
			f.delete();
		}
		if (f2.exists()) {
			f2.delete();
		}
	}

	@Test
	public void testEncryption() throws IOException {
		// CSVFile file = new CSVFile(new File(this.testfile));

		InputStream is = file.upload();
		OutputStream out = new FileOutputStream(new File(
				this.encryptedOutputFile));

		byte[] buffer = new byte[1024];
		int numRead = 0;
		while ((numRead = is.read(buffer)) >= 0) {
			out.write(buffer, 0, numRead);
		}
		// is.close();
		// // out.flush();
		// out.close();
		//

		file.finishedUpload();
		Assert.assertNotNull(file.getCapability());
		Assert.assertNotNull(file.getCapability().getVerificationKey());
		Assert.assertTrue(file.isValid());
		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8, file.getCapability()
				.getVerificationKey().length);

	}

	@Test
	public void testDecryption() throws IOException {
		testEncryption();
		CSVFile f = new CSVFile(file.getCapability(), new File(
				this.decryptedOutputFile));
		OutputStream out = f.download();

		InputStream is = new FileInputStream(new File(this.encryptedOutputFile));

		byte[] buffer = new byte[2048];
		int numRead = 0;

		while ((numRead = is.read(buffer)) >= 0) {
			out.write(buffer, 0, numRead);
		}
		is.close();
		out.flush();
		out.close();
		f.finishedDownload();
		Assert.assertTrue(f.isValid());

		File plainFile = new File(this.testfile);
		File decFile = new File(this.decryptedOutputFile);
		byte[] plain = FileUtils.readDataBinary(new FileInputStream(plainFile),
				(int) plainFile.length());
		byte[] dec = FileUtils.readDataBinary(new FileInputStream(decFile),
				(int) decFile.length());
		Assert.assertArrayEquals(plain, dec);

	}

	@Test
	public void testThatFileVerificationIsSetCorrectly() throws IOException {
		InputStream is = this.file.upload();
		OutputStream out = new FileOutputStream(new File(
				this.encryptedOutputFile));

		byte[] buffer = new byte[2048];
		int numRead = 0;
		while ((numRead = is.read(buffer)) >= 0) {
			out.write(buffer, 0, numRead);
		}
		this.file.finishedUpload();

		byte[] recorded = this.file.getCapability().getVerificationKey();

		byte[] tmp = FileUtils.readDataBinary(
				new FileInputStream(this.file.getFile()), (int) this.file
						.getFile().length());

		byte[] correctHash = Cryptoutil.hash(tmp, Cryptoutil.SYM_SIZE / 8);
		Assert.assertArrayEquals(correctHash, recorded);

	}

	@Test
	public void testContentLength() throws FileNotFoundException, IOException {
		long len = this.file.getContentLength();
		byte[] tmp = FileUtils.readDataBinary(
				new FileInputStream(this.file.getFile()), (int) this.file
						.getFile().length());

		SecretKey sk = new SecretKeySpec(this.file.getCapability().getKey(),
				Cryptoutil.SYM_CIPHER);
		IvParameterSpec iv = new IvParameterSpec(Cryptoutil.nHash(
				sk.getEncoded(), 2, Cryptoutil.SYM_BLOCK_SIZE / 8));

		byte[] cipher = Cryptoutil.symEncrypt(tmp, sk, iv);
		Assert.assertEquals(cipher.length, (int) len);

	}
}
