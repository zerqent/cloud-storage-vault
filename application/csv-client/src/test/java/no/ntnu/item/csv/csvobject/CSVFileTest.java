package no.ntnu.item.csv.csvobject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import no.ntnu.item.csv.fileutils.FileUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileTest {

	private CSVFile csvFile;
	private final String testfile = "src/test/resources/smallloremipsum.txt";

	@Before
	public void setUp() throws IOException {
		this.csvFile = new CSVFile(new File(this.testfile));
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
		CSVFile newcsv = new CSVFile(this.csvFile.getCapability(),
				this.csvFile.getCipherText());
		newcsv.decrypt();
		Assert.assertArrayEquals(this.csvFile.getPlainText(),
				newcsv.getPlainText());
	}

	@Test
	public void testEncryptandDecryptAFile() throws IOException {
		File file = new File("src/test/resources/smallloremipsum.txt");
		this.csvFile.setPlainText(file);
		this.csvFile.encrypt();

		InputStream in = getClass().getResourceAsStream("/smallloremipsum.txt");

		byte[] plainText = FileUtils.readDataBinary(in, (int) file.length());
		CSVFile newcsv = new CSVFile(this.csvFile.getCapability(),
				this.csvFile.getCipherText());
		newcsv.decrypt();
		Assert.assertArrayEquals(plainText, newcsv.getPlainText());
	}
}
