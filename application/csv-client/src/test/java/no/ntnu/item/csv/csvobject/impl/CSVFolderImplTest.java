package no.ntnu.item.csv.csvobject.impl;

import junit.framework.Assert;
import no.ntnu.item.csv.capability.CapabilityType;

import org.junit.Before;
import org.junit.Test;

public class CSVFolderImplTest extends CSVFolderImpl{

	private CSVFolderImpl csvFolder;
	
	@Before
	public void setUp() {
		this.csvFolder = new CSVFolderImpl();
	}
	
	@Test
	public void testCapabilityGeneration() {
		Assert.assertNotNull(this.csvFolder.getCapabilitites());
		
		Assert.assertTrue(this.csvFolder.getCapabilitites().containsKey(CapabilityType.RW));
		Assert.assertEquals(16, this.csvFolder.getCapabilitites().get(CapabilityType.RW).getKey().length);
		Assert.assertEquals(16, this.csvFolder.getCapabilitites().get(CapabilityType.RW).getVerificationKey().length);
		
		Assert.assertTrue(this.csvFolder.getCapabilitites().containsKey(CapabilityType.RO));
		Assert.assertEquals(16, this.csvFolder.getCapabilitites().get(CapabilityType.RO).getKey().length);
		Assert.assertEquals(16, this.csvFolder.getCapabilitites().get(CapabilityType.RO).getVerificationKey().length);
		
		Assert.assertTrue(this.csvFolder.getCapabilitites().containsKey(CapabilityType.V));
		Assert.assertEquals(16, this.csvFolder.getCapabilitites().get(CapabilityType.V).getVerificationKey().length);
	}
		
}
