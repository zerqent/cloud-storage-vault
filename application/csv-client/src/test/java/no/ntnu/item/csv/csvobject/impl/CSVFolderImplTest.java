package no.ntnu.item.csv.csvobject.impl;

import javax.crypto.SecretKey;

import junit.framework.Assert;
import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

import org.junit.Before;
import org.junit.Test;

public class CSVFolderImplTest {

	private CSVFolderImpl newFolder;

	@Before
	public void setUp() {
		this.newFolder = new CSVFolderImpl();
	}

	@Test
	public void testCapabilityGeneration() {
		Capability cap = this.newFolder.getCapability();	
		Assert.assertEquals(CapabilityType.RW, cap.getType());
		Assert.assertNotNull(cap.getStorageIndex());
		Assert.assertEquals(16, cap.getVerificationKey().length);
		Assert.assertEquals(16, cap.getKey().length);
	}
	
	@Test
	public void testEncryption() {
		SecretKey key = Cryptoutil.generateSymmetricKey();
		Capability cap = new CapabilityImpl(CapabilityType.RO, key.getEncoded(), null);
		this.newFolder.addContent("Hallo", cap);
		this.newFolder.encrypt();
		Assert.assertNotNull(this.newFolder.getCipherText());
		
	}
	
	@Test
	public void testDecryption() {
		testEncryption();
		CSVFolderImpl dec = new CSVFolderImpl(this.newFolder.getCapability(), this.newFolder.getCipherText(), this.newFolder.getPubKey(), this.newFolder.getIV(), null);
		dec.decrypt();
		Assert.assertTrue(dec.getContents().containsKey("Hallo"));
	}
	
	@Test
	public void testSigning() {
		testEncryption();
		Assert.assertTrue(this.newFolder.isValid());
	}

	
}
