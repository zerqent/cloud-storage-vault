package no.ntnu.item.csv.csvobject;

import javax.crypto.SecretKey;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.testutils.ArrayComparison;
import android.test.ActivityInstrumentationTestCase2;

public class CSVFolderImplTest extends
		ActivityInstrumentationTestCase2<CSVActivity> {

	private CSVFolder newFolder;

	public CSVFolderImplTest() {
		super("no.ntnu.item.csv", CSVActivity.class);
	}

	@Override
	public void setUp() {
		this.newFolder = new CSVFolder();
	}

	public void testCapabilityGeneration() {
		Capability cap = this.newFolder.getCapability();
		assertEquals(CapabilityType.RW, cap.getType());
		assertNotNull(cap.getStorageIndex());
		assertEquals(16, cap.getVerificationKey().length);
		assertEquals(16, cap.getKey().length);
	}

	public void testEncryption() {
		SecretKey key = Cryptoutil.generateSymmetricKey();
		Capability cap = new CapabilityImpl(CapabilityType.RO,
				key.getEncoded(), null, true);
		this.newFolder.addContent("Hallo", cap);
		this.newFolder.encrypt();
		assertNotNull(this.newFolder.getCipherText());

	}

	public void testSigning() {
		testEncryption();
		assertTrue(this.newFolder.isValid());
	}

	public void testSerialization() {
		CapabilityImpl cap = new CapabilityImpl(CapabilityType.RO, Cryptoutil
				.generateSymmetricKey().getEncoded(), null, true);
		this.newFolder.addContent("Foobar", cap);
		this.newFolder.encrypt();
		byte[] enc = this.newFolder.getTransferArray();
		int expectedLength = 1 + 272 + 132 + 128 + 16
				+ this.newFolder.getCipherText().length;
		// identifier + encPrivkey + pubkey + signature + iv + Ciphertext
		assertEquals(expectedLength, enc.length);
		CSVFolder dec = CSVFolder.createFromByteArray(enc,
				this.newFolder.getCapability());
		assertTrue(ArrayComparison.arraysAreEqual(
				this.newFolder.getCipherText(), dec.getCipherText()));
		assertTrue(ArrayComparison.arraysAreEqual(this.newFolder.getPubKey(),
				dec.getPubKey()));
		dec.decrypt();
		dec.encrypt();
		assertTrue(ArrayComparison.arraysAreEqual(
				this.newFolder.getPlainText(), dec.getPlainText()));
		assertTrue(dec.getContents().containsKey("Foobar"));
		assertTrue(ArrayComparison.arraysAreEqual(cap.getKey(), dec
				.getContents().get("Foobar").getKey()));
		assertEquals(cap.getType(), dec.getContents().get("Foobar").getType());

		assertEquals(enc.length, dec.getTransferArray().length);
		// Assert.assertArrayEquals(enc, dec.getTransferArray()); // Signature
		// will always be different.
	}

}
