package no.ntnu.item.csv.cryptoutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import no.ntnu.item.cryptoutil.CryptoStreamer;
import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CryptoStreamerTest {

	private CryptoStreamer cryptoStreamer;
	private final String testfile = "src/test/resources/smallloremipsum.txt";
	private final String encryptedOutputFile = "/tmp/testenc.txt";
	private final String decryptedOutputFile = "/tmp/testdec.txt";
	private byte[] tmpDigest;

	@Before
	public void setUp() {
		this.cryptoStreamer = new CryptoStreamer();
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
	public void testEncryptionAndDecryptionToMemory() throws IOException {
		InputStream is = new FileInputStream(testfile);
		byte[] cipherText = this.cryptoStreamer.encrypt(is);
		Assert.assertNotNull(cipherText);
		Assert.assertTrue(cipherText.length > 2);

		byte[] plainText = Cryptoutil
				.symDecrypt(cipherText, this.cryptoStreamer.getSecretKey(),
						this.cryptoStreamer.getIv());
		File f = new File(testfile);
		FileInputStream fis = new FileInputStream(f);
		Assert.assertArrayEquals(
				FileUtils.readDataBinary(fis, (int) f.length()), plainText);

	}

	@Test
	public void testEncryptionOfStream() throws IOException {
		FileInputStream fis = new FileInputStream(this.testfile);
		InputStream foo = this.cryptoStreamer
				.getEncryptedAndHashedInputStream(fis);
		FileOutputStream out = new FileOutputStream(this.encryptedOutputFile);

		byte[] buffer = new byte[1024];
		int numRead = 0;

		while ((numRead = foo.read(buffer)) >= 0) {
			out.write(buffer, 0, numRead);
		}

		this.tmpDigest = this.cryptoStreamer.finish();
		Assert.assertEquals(16, tmpDigest.length);
		byte[] cipher = FileUtils.readDataBinary(new FileInputStream(
				this.encryptedOutputFile), (int) new File(
				this.encryptedOutputFile).length());
		Assert.assertTrue(cipher.length > 10);

		byte[] plain = Cryptoutil
				.symDecrypt(cipher, this.cryptoStreamer.getSecretKey(),
						this.cryptoStreamer.getIv());

		File f = new File(this.testfile);
		byte[] orgPlain = FileUtils.readDataBinary(new FileInputStream(f),
				(int) f.length());
		Assert.assertArrayEquals(orgPlain, plain);

	}

	@Test
	public void testDecryptionOfStream() throws IOException {
		testEncryptionOfStream();

		FileOutputStream os = new FileOutputStream(this.decryptedOutputFile);
		OutputStream ros = this.cryptoStreamer
				.getDecryptedAndHashedOutputStream(os);
		FileInputStream fin = new FileInputStream(this.encryptedOutputFile);

		byte[] buffer = new byte[1024];
		int numRead = 0;

		while ((numRead = fin.read(buffer)) >= 0) {
			ros.write(buffer, 0, numRead);
		}
		ros.flush();
		ros.close();
		byte[] digest = this.cryptoStreamer.finish();
		Assert.assertEquals(16, digest.length);
		Assert.assertArrayEquals(this.tmpDigest, digest);

		File f = new File(this.decryptedOutputFile);
		byte[] plain = FileUtils.readDataBinary(new FileInputStream(f),
				(int) f.length());
		f = new File(this.testfile);
		byte[] realplain = FileUtils.readDataBinary(new FileInputStream(f),
				(int) f.length());
		Assert.assertArrayEquals(realplain, plain);

	}

	@Test
	public void testThatADigestStreamIsTransparent()
			throws FileNotFoundException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {

		// Does not test our functionality, just makes sure that this works.

		Cipher cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKey sk = Cryptoutil.generateSymmetricKey();
		cip.init(Cipher.ENCRYPT_MODE, sk);
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		FileInputStream fis = new FileInputStream(testfile);
		DigestInputStream dig = new DigestInputStream(fis, md);
		CipherInputStream cipI = new CipherInputStream(dig, cip);

		FileOutputStream fos = new FileOutputStream("/tmp/test.txt");

		byte buffer[] = new byte[1024];

		int numRead = 0;
		try {
			while ((numRead = cipI.read(buffer)) >= 0) {
				fos.write(buffer, 0, numRead);
			}
			cipI.close();

			byte[] tmpdigest = dig.getMessageDigest().digest();
			// System.out.println(getHex(tmpdigest));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] iv = cip.getIV();
		cip.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(iv));
		fis = new FileInputStream("/tmp/test.txt");

		fos = new FileOutputStream("/tmp/decrypted.txt");
		DigestOutputStream digO = new DigestOutputStream(fos, md);
		CipherOutputStream cipO = new CipherOutputStream(digO, cip);

		buffer = new byte[2048];
		numRead = 0;
		try {
			while ((numRead = fis.read(buffer)) >= 0) {
				cipO.write(buffer, 0, numRead);
			}
			fis.close();
			cipO.flush();
			cipO.close();

			byte[] digest = dig.getMessageDigest().digest();
			// System.out.println(getHex(digest));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static final String HEXES = "0123456789ABCDEF";

	public static String getHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

}
