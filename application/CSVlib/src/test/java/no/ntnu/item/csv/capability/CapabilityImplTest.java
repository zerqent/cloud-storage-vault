package no.ntnu.item.csv.capability;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.contrib.com.bitzi.util.Base32;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CapabilityImplTest {

	private CapabilityImpl cap;
	private CapabilityImpl rwcap;

	@Before
	public void setUp() throws Exception {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		SecretKey sk = Cryptoutil.generateSymmetricKey();
		this.cap = new CapabilityImpl(CapabilityType.RO, sk.getEncoded(), null,
				false);
		this.rwcap = new CapabilityImpl(CapabilityType.RW, sk.getEncoded(),
				null, false);
	}

	@After
	public void teardown() {

	}

	@Test
	public void testStorageIndexEncoding() {
		String base32 = this.cap.getStorageIndex();
		Assert.assertEquals((int) Math.ceil(Cryptoutil.SYM_SIZE / 5.0),
				base32.length());
		Assert.assertTrue(isBase32(base32));
		Assert.assertArrayEquals(this.cap.getStorageIndexByte(),
				Base32.decode(this.cap.getStorageIndex()));
	}

	@Test
	public void testStorageIndexGeneration() {
		Assert.assertEquals(this.cap.getStorageIndex(), Base32
				.encode(Cryptoutil.nHash(this.cap.getKey(), 1,
						Cryptoutil.SYM_SIZE / 8)));
		Assert.assertEquals(this.rwcap.getStorageIndex(), Base32
				.encode(Cryptoutil.nHash(this.cap.getStorageIndexByte(), 1,
						Cryptoutil.SYM_SIZE / 8)));
	}

	@Test
	public void testEncodingAndDecoding() {
		String encoded = this.cap.toString();
		Capability decoded = CapabilityImpl.fromString(encoded);
		Assert.assertArrayEquals(this.cap.getKey(), decoded.getKey());
		Assert.assertEquals(this.cap.getType(), decoded.getType());
		Assert.assertEquals(this.cap.getStorageIndex(),
				decoded.getStorageIndex());
		Assert.assertEquals(this.cap.getVerificationKey(),
				decoded.getVerificationKey());
		Assert.assertEquals(this.cap.isFile(), decoded.isFile());
	}

	@Test
	public void testWriteEnablerGeneration() {
		Assert.assertNull(this.cap.getWriteEnabler());
		CapabilityImpl capimpl = new CapabilityImpl(CapabilityType.RW,
				Cryptoutil.generateSymmetricKey().getEncoded(), null, false);
		Assert.assertNotNull(capimpl.getWriteEnabler());
	}

	@Test
	public void testKeyLengths() {
		SecretKey sks = Cryptoutil.generateSymmetricKey();
		SecretKey verify = Cryptoutil.generateSymmetricKey();

		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8, sks.getEncoded().length);
		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8, verify.getEncoded().length);

		Capability cap = new CapabilityImpl(CapabilityType.RW,
				sks.getEncoded(), verify.getEncoded(), false);

		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8, cap.getKey().length);
		Assert.assertEquals(Cryptoutil.SYM_SIZE / 8,
				cap.getVerificationKey().length);
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
