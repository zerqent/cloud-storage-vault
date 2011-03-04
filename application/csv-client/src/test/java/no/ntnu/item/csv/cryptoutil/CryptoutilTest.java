package no.ntnu.item.csv.cryptoutil;

import java.security.KeyPair;
import java.security.MessageDigest;

import no.ntnu.item.cryptoutil.Cryptoutil;

import org.junit.Assert;
import org.junit.Test;

public class CryptoutilTest {


	@Test
	public void testNormalHashing() throws Exception {
		byte[] test = {50,100,34,56,78};

		byte[] res = Cryptoutil.hash(test, 0);
		Assert.assertEquals(32, res.length);

		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);
		md.update(test);
		Assert.assertArrayEquals(md.digest(), res);
	}

	@Test
	public void testTruncatedHashing() throws Exception {
		byte[] test = {50,100,34,56,78};

		byte[] res = Cryptoutil.hash(test, 16);
		Assert.assertEquals(16, res.length);

		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);
		md.update(test);
		byte[] res2 = md.digest();
		byte[] trunc = new byte[16];

		for (int i = 0; i < trunc.length; i++) {
			trunc[i] = res2[i];
		}

		Assert.assertArrayEquals(trunc, res);
	}

	@Test
	public void testNonTruncatedNHashing() throws Exception{
		byte[] msg = {50,100,34,56,78,12,56,13,16,68,44,45,76,45,34,12};
		byte[] store = msg;
		byte[] tmp = store;
		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);

		for (int i=1; i<15; i++) {
			md.reset();
			md.update(store);
			tmp = md.digest();
			Assert.assertArrayEquals(tmp, Cryptoutil.nHash(msg, i, 0));
			store = tmp;
		}

	}

	@Test
	public void testTruncatedNHashing() throws Exception {
		byte[] msg = {50,100,34,56,78,12,56,13,16,68,44,45,76,45,34,12};
		byte[] store;
		byte[] truncated;
		int truncate_to;

		for (int k = 2; k < 33; k=k*2) {
			truncate_to = k;
			truncated = new byte[truncate_to];
			store = msg;
			MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);

			for (int i=1; i<5; i++) {
				md.reset();
				md.update(store);
				store = md.digest();

				for (int j = 0; j < truncate_to; j++) {
					truncated[j] = store[j];
				}
				store = truncated;
				byte[] tmp = Cryptoutil.nHash(msg, i, truncate_to);
				Assert.assertEquals(truncate_to, tmp.length);
				Assert.assertArrayEquals(store, tmp);
			}
		}
	}

	@Test
	public void testSigning() {
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		byte[] msg = {50,100,34,56,78,12,56,13,16,68,44,45,76,45,34,12};
		byte[] hashed = Cryptoutil.hash(msg, -1);
		byte[] sign = Cryptoutil.signature(hashed, pair.getPrivate().getEncoded());
		Assert.assertTrue(Cryptoutil.signature_valid(sign, hashed, pair.getPublic().getEncoded()));

		KeyPair pair2 = Cryptoutil.generateAsymmetricKeys(); 
		Assert.assertFalse(Cryptoutil.signature_valid(sign, hashed, pair2.getPublic().getEncoded()));
	}

}
