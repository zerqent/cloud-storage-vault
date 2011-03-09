package no.ntnu.item.csv.csvobject.impl;

import javax.crypto.SecretKey;

import org.junit.Assert;
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

	@Test
	public void testSerialization() {
		CapabilityImpl cap = new CapabilityImpl(CapabilityType.RO, Cryptoutil.generateSymmetricKey().getEncoded(), null);
		this.newFolder.addContent("Foobar", cap);
		this.newFolder.encrypt();
		byte[] enc = this.newFolder.getTransferArray();
		int expectedLength = 1 + 272 + 132 + 128 + 16 + this.newFolder.getCipherText().length;
		// identifier + encPrivkey + pubkey + signature + iv + Ciphertext
		Assert.assertEquals(expectedLength, enc.length);
		CSVFolderImpl dec = CSVFolderImpl.createFromByteArray(enc, this.newFolder.getCapability());
		Assert.assertArrayEquals(this.newFolder.getCipherText(), dec.getCipherText());
		Assert.assertArrayEquals(this.newFolder.getPubKey(), dec.getPubKey());
		dec.decrypt();
		dec.encrypt();
		Assert.assertArrayEquals(this.newFolder.getPlainText(), dec.getPlainText());
		Assert.assertTrue(dec.getContents().containsKey("Foobar"));
		Assert.assertArrayEquals(cap.getKey(), dec.getContents().get("Foobar").getKey());
		Assert.assertEquals(cap.getType(), dec.getContents().get("Foobar").getType());

		Assert.assertEquals(enc.length, dec.getTransferArray().length);
		//Assert.assertArrayEquals(enc, dec.getTransferArray()); // Signature will always be different.
	}


}
