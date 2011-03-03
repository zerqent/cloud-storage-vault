package no.ntnu.item.csv.csvobject.impl;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileFacadeTest {
	
	private CSVFileFacade facade;
	private byte[] plainText = {50,100,37,47,52,32};
	
	@Before
	public void setUp() {
		this.facade = new CSVFileFacade();
	}
	
	@After
	public void teardown() {
	}
	
	@Test
	public void testEncryption() {
		this.facade.setPlainText(this.plainText);
		this.facade.encrypt();
		
		Assert.assertNotNull(this.facade.getCipherText());
		Assert.assertNotNull(this.facade.getCapability());
		
	}
	
	@Test
	public void testDecryption() {
		testEncryption();
		CSVFileFacade fac = new CSVFileFacade();
		fac.setCipherText(this.facade.getCipherText());
		fac.setCapability(this.facade.getCapability());
		fac.decrypt();
		Assert.assertArrayEquals(this.plainText, fac.getPlainText());
	}
	
	@Test
	public void testCapability() {
		testEncryption();
		Capability cap = this.facade.getCapability();
		Assert.assertEquals(CapabilityType.READ_ONLY, cap.getType());
		Assert.assertEquals(16, cap.getKey().length);
	}
	
}
