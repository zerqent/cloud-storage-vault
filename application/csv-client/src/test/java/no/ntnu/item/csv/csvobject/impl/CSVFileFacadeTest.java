package no.ntnu.item.csv.csvobject.impl;

import java.io.File;
import java.io.IOException;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileFacadeTest {

	private CSVFileFacade facade;
	private final String testfile = "src/test/resources/smallloremipsum.txt";

	@Before
	public void setUp() throws IOException {
		this.facade = new CSVFileImpl(new File(this.testfile));
	}

	@After
	public void teardown() {
	}

	@Test
	public void testEncryption() {
		//this.facade.setPlainText(this.plainText);
		this.facade.encrypt();

		Assert.assertNotNull(this.facade.getCipherText());
		Assert.assertNotNull(this.facade.getCapability());

	}

	@Test
	public void testDecryption() {
		testEncryption();
		CSVFileFacade fac = new CSVFileImpl(this.facade.getCapability(), this.facade.getCipherText());
		fac.decrypt();
		Assert.assertArrayEquals(this.facade.getPlainText(), fac.getPlainText());
	}

	@Test
	public void testCapability() {
		testEncryption();
		Capability cap = this.facade.getCapability();
		Assert.assertEquals(CapabilityType.RO, cap.getType());
		Assert.assertEquals(16, cap.getKey().length);
	}

}
