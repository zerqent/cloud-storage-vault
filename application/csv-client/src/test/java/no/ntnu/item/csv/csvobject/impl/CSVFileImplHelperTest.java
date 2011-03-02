package no.ntnu.item.csv.csvobject.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileImplHelperTest {
	
	private CSVFileImplHelper csvFile;
	private byte[] msg = {0x10,0x20};
	
	@Before
	public void setUp() {
		this.csvFile = new CSVFileImplHelper();
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testThatEncryptionDoesNotCauseErrors() {
		Assert.assertFalse(this.csvFile.isPlainTextReady());
		Assert.assertFalse(this.csvFile.isCipherTextReady());
		this.csvFile.setPlainText(this.msg);
		Assert.assertTrue(this.csvFile.isPlainTextReady());
		Assert.assertFalse(this.csvFile.isCipherTextReady());
		this.csvFile.encrypt();
		Assert.assertTrue(this.csvFile.isCipherTextReady());
	}
	
	@Test
	public void testThatEncryptionAndDecryptionDoesNotCauseErrors() {
		this.testThatEncryptionDoesNotCauseErrors();
		this.csvFile.decrypt();
		Assert.assertArrayEquals(this.msg, this.csvFile.getPlainText());	
	}
	
	@Test
	public void testThatDecryptionOfNewObjectDoesNotCauseerrors() {
		this.testThatEncryptionDoesNotCauseErrors();
		CSVFileImplHelper newcsv = new CSVFileImplHelper();
		newcsv.setCipherText(this.csvFile.getCipherText());
		newcsv.setIV(this.csvFile.getIV());
		newcsv.setSecretKey(this.csvFile.getSecretKey());
		Assert.assertTrue(this.csvFile.isCipherTextReady());
		Assert.assertArrayEquals(this.csvFile.getSecretKey(),newcsv.getSecretKey());
		newcsv.decrypt();
		Assert.assertArrayEquals(this.msg, newcsv.getPlainText());
		Assert.assertTrue(this.csvFile.isPlainTextReady());
		
	}
	
	@Test
	public void testBasicPlainTextDigest() throws NoSuchAlgorithmException {
		Assert.assertNull(this.csvFile.getPlainTextDigest());
		this.csvFile.setPlainText(this.msg);
		byte[] md1 = this.csvFile.getPlainTextDigest();
		Assert.assertNotNull(md1);
		
		Assert.assertEquals(32, md1.length);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(this.msg);
		Assert.assertArrayEquals(md1, md.digest());
	}
	
	@Test
	public void plainTextDigestShouldBetheSameForSamePlainText() {
		this.csvFile.setPlainText(this.msg);
		byte[] md1 = this.csvFile.getPlainTextDigest();
		
		CSVFileImplHelper newcsv = new CSVFileImplHelper();
		newcsv.setPlainText(this.msg);
		Assert.assertArrayEquals(md1,newcsv.getPlainTextDigest());
	}
	
	@Test
	public void testCipherTextDigest() {
		Assert.assertNull(this.csvFile.getCipherTextDigest());
		this.testThatEncryptionDoesNotCauseErrors();
		byte[] md1 = this.csvFile.getCipherTextDigest();
		Assert.assertNotNull(md1);
		
		Assert.assertEquals(32, md1.length);
	}
	
	@Test
	public void cipherTextDigestShouldBeDifferentForEveryNewInstance() {
		this.testThatEncryptionDoesNotCauseErrors();
		byte[] md1 = this.csvFile.getCipherTextDigest();
		
		CSVFileImplHelper newcsv = new CSVFileImplHelper();
		newcsv.setPlainText(this.msg);
		newcsv.encrypt();
		byte[] md2 = newcsv.getCipherTextDigest(); 
		
		Assert.assertEquals(md1.length, md2.length);
		Assert.assertFalse(md1.toString().equals(md2.toString()));
	}
}
