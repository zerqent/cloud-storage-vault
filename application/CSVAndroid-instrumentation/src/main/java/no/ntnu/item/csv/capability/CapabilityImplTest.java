package no.ntnu.item.csv.capability;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;
import no.ntnu.item.csv.testutils.ArrayComparison;
import android.test.ActivityInstrumentationTestCase2;

public class CapabilityImplTest extends
		ActivityInstrumentationTestCase2<CSVActivity> {

	public CapabilityImplTest() {
		super("no.ntnu.item.csv", CSVActivity.class);
	}

	private CapabilityImpl cap;
	private CapabilityImpl rwcap;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey sk = Cryptoutil.generateSymmetricKey();
		this.cap = new CapabilityImpl(CapabilityType.RO, sk.getEncoded(), null,
				true);
		this.rwcap = new CapabilityImpl(CapabilityType.RW, sk.getEncoded(),
				null, true);
	}

	public void testStorageIndexEncoding() {
		String base32 = this.cap.getStorageIndex();
		assertEquals(26, base32.length());
		assertTrue(isBase32(base32));
		// assertArrayEquals(this.cap.getStorageIndexByte(),
		// Base32.decode(this.cap.getStorageIndex()));
		assertTrue(ArrayComparison.arraysAreEqual(
				this.cap.getStorageIndexByte(),
				Base32.decode(this.cap.getStorageIndex())));
	}

	public void testStorageIndexGeneration() {
		assertEquals(this.cap.getStorageIndex(),
				Base32.encode(Cryptoutil.nHash(this.cap.getKey(), 1, 16)));
		assertEquals(this.rwcap.getStorageIndex(), Base32.encode(Cryptoutil
				.nHash(this.cap.getStorageIndexByte(), 1, 16)));
	}

	public void testEncodingAndDecoding() {
		String encoded = this.cap.toString();
		Capability decoded = CapabilityImpl.fromString(encoded);
		assertTrue(ArrayComparison.arraysAreEqual(this.cap.getKey(),
				decoded.getKey()));
		assertEquals(this.cap.getType(), decoded.getType());
		assertEquals(this.cap.getStorageIndex(), decoded.getStorageIndex());
		assertEquals(this.cap.getVerificationKey(),
				decoded.getVerificationKey());
	}

	public void testWriteEnablerGeneration() {
		assertNull(this.cap.getWriteEnabler());
		CapabilityImpl capimpl = new CapabilityImpl(CapabilityType.RW,
				Cryptoutil.generateSymmetricKey().getEncoded(), null, true);
		assertNotNull(capimpl.getWriteEnabler());
		assertEquals(16, capimpl.getWriteEnabler().length);
	}

	public boolean isBase32(String input) {
		String base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

		char[] base32Array = input.toCharArray();
		char[] base32CharsArray = base32Chars.toCharArray();

		for (int i = 0; i < base32Array.length; i++) {
			boolean isBase32 = false;
			for (int j = 0; j < base32CharsArray.length; j++) {
				if (base32Array[i] == base32CharsArray[j]) {
					isBase32 = true;
					break;
				}
			}
			if (!isBase32) {
				return false;
			}
		}
		return true;
	}

}
