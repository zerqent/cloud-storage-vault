package no.ntnu.item.csv.csvobject;

import javax.crypto.SecretKey;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFolderTest {

	private CSVFolder newFolder;

	@Before
	public void setUp() {
		this.newFolder = new CSVFolder();
	}

	@Test
	public void testCapabilityGeneration() {
		Capability cap = this.newFolder.getCapability();
		Assert.assertEquals(CapabilityType.RW, cap.getType());
		Assert.assertNotNull(cap.getStorageIndex());
		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8,
				cap.getVerificationKey().length);
		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8, cap.getKey().length);
	}

	@Test
	public void testEncryption() {
		SecretKey key = Cryptoutil.generateSymmetricKey();
		Capability cap = new CapabilityImpl(CapabilityType.RO,
				key.getEncoded(), null, false);
		this.newFolder.addContent("Hallo", cap);
		this.newFolder.upload();
		Assert.assertNotNull(this.newFolder.getCipherText());

	}

	@Test
	public void testDecryption() {
		testEncryption();

		CSVFolder decFolder = new CSVFolder(this.newFolder.getCapability());
		decFolder.download(this.newFolder.upload());

		Assert.assertTrue(decFolder.getContents().containsKey("Hallo"));
		Assert.assertTrue(decFolder.isValid());
	}

	@Test
	public void testSigningOfInitial() {
		testEncryption();
		Assert.assertTrue(this.newFolder.isValid());
	}

}
