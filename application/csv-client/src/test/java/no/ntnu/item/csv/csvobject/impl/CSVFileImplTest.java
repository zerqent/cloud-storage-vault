package no.ntnu.item.csv.csvobject.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileImplTest {

	private CSVFileImpl csvFile;
	private final String testfile = "src/test/resources/smallloremipsum.txt";

	@Before
	public void setUp() throws IOException {
		this.csvFile = new CSVFileImpl(new File(this.testfile));
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testThatEncryptionDoesNotCauseErrors() {
		this.csvFile.encrypt();
	}

	@Test
	public void testThatEncryptionAndDecryptionDoesNotCauseErrors() {
		this.testThatEncryptionDoesNotCauseErrors();
		byte[] tmp = this.csvFile.getPlainText();
		this.csvFile.decrypt();
		Assert.assertArrayEquals(tmp, this.csvFile.getPlainText());	
	}

	@Test
	public void testThatDecryptionOfNewObjectDoesNotCauseerrors() {
		this.testThatEncryptionDoesNotCauseErrors();
		CSVFileImpl newcsv = new CSVFileImpl(this.csvFile.getCapability(), this.csvFile.getCipherText());	
		newcsv.decrypt();
		Assert.assertArrayEquals(this.csvFile.getPlainText(), newcsv.getPlainText());
	}


	@Test
	public void testEncryptandDecryptAFile() throws IOException {
		File file = new File("src/test/resources/smallloremipsum.txt");
		this.csvFile.setPlainText(file);
		this.csvFile.encrypt();

		InputStream in = getClass().getResourceAsStream( "/smallloremipsum.txt" );

		byte[] plainText = CSVFileImpl.readDataBinary(in, (int)file.length());
		CSVFileImpl newcsv = new CSVFileImpl(this.csvFile.getCapability(), this.csvFile.getCipherText());
		newcsv.decrypt();
		Assert.assertArrayEquals(plainText, newcsv.getPlainText());
	}
}
