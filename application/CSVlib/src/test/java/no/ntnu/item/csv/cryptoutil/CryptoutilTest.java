package no.ntnu.item.csv.cryptoutil;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import no.ntnu.item.cryptoutil.Cryptoutil;

import org.junit.Assert;
import org.junit.Test;

public class CryptoutilTest {

	@Test
	public void testNormalHashing() throws Exception {
		byte[] test = { 50, 100, 34, 56, 78 };

		byte[] res = Cryptoutil.hash(test, 0);
		Assert.assertEquals(32, res.length);

		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);
		md.update(test);
		byte[] tmp = md.digest();
		md.reset();
		md.update(tmp);
		Assert.assertArrayEquals(md.digest(), res);
	}

	@Test
	public void testTruncatedHashing() throws Exception {
		byte[] test = { 50, 100, 34, 56, 78 };

		byte[] res = Cryptoutil.hash(test, 16);
		Assert.assertEquals(16, res.length);

		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);
		md.update(test);
		byte[] tmp = md.digest();
		md.reset();
		md.update(tmp);
		byte[] res2 = md.digest();
		byte[] trunc = new byte[16];

		for (int i = 0; i < trunc.length; i++) {
			trunc[i] = res2[i];
		}

		Assert.assertArrayEquals(trunc, res);
	}

	@Test
	public void testNonTruncatedNHashing() throws Exception {
		byte[] msg = { 50, 100, 34, 56, 78, 12, 56, 13, 16, 68, 44, 45, 76, 45,
				34, 12 };
		byte[] store = msg;
		byte[] tmp = store;
		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);

		for (int i = 1; i < 15; i++) {
			md.reset();
			md.update(store);
			tmp = md.digest();
			md.reset();
			md.update(tmp);
			tmp = md.digest();
			Assert.assertArrayEquals(tmp, Cryptoutil.nHash(msg, i, 0));
			store = tmp;
		}

	}

	@Test
	public void testTruncatedNHashing() throws Exception {
		byte[] msg = { 50, 100, 34, 56, 78, 12, 56, 13, 16, 68, 44, 45, 76, 45,
				34, 12 };
		byte[] store;
		byte[] truncated;
		int truncate_to;

		for (int k = 2; k < 33; k = k * 2) {
			truncate_to = k;
			truncated = new byte[truncate_to];
			store = msg;
			MessageDigest md = MessageDigest
					.getInstance(Cryptoutil.HASH_ALGORITHM);

			for (int i = 1; i < 5; i++) {
				md.reset();
				md.update(store);
				store = md.digest();
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
		byte[] msg = { 50, 100, 34, 56, 78, 12, 56, 13, 16, 68, 44, 45, 76, 45,
				34, 12 };
		byte[] hashed = Cryptoutil.hash(msg, -1);
		byte[] sign = Cryptoutil.signature(hashed, pair.getPrivate());
		Assert.assertTrue(Cryptoutil.signature_valid(sign, hashed,
				pair.getPublic()));

		KeyPair pair2 = Cryptoutil.generateAsymmetricKeys();
		Assert.assertFalse(Cryptoutil.signature_valid(sign, hashed,
				pair2.getPublic()));
	}

	@Test
	public void testRSAKeyGeneration() {
		int TEST_AMOUNT = 15;

		// We need keys to be of a static size //TODO: Figure out why
		// PrivateExponent.length is sometimes 129
		for (int i = 0; i < TEST_AMOUNT; i++) {
			KeyPair pair = Cryptoutil.generateAsymmetricKeys();
			RSAPrivateKey priv = (RSAPrivateKey) pair.getPrivate();
			RSAPublicKey pub = (RSAPublicKey) pair.getPublic();

			Assert.assertEquals(Cryptoutil.ASYM_SIZE / 8 + 1, priv.getModulus()
					.toByteArray().length);
			Assert.assertEquals(Cryptoutil.ASYM_SIZE / 8 + 1, pub.getModulus()
					.toByteArray().length);
			// Assert.assertEquals(128,
			// priv.getPrivateExponent().toByteArray().length);
			Assert.assertEquals(3, pub.getPublicExponent().toByteArray().length);
		}
	}

	@Test
	public void testRSASerialization() {
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		RSAPrivateKey rsaPriv = (RSAPrivateKey) pair.getPrivate();
		RSAPublicKey rsaPub = (RSAPublicKey) pair.getPublic();

		byte[] b_priv = Cryptoutil.serializePrivateKey(rsaPriv);
		byte[] b_pub = Cryptoutil.serializePublicKey(rsaPub);

		RSAPrivateKey serPriv = (RSAPrivateKey) Cryptoutil
				.createRSAPrivateKey(b_priv);
		RSAPublicKey serPub = (RSAPublicKey) Cryptoutil
				.createRSAPublicKey(b_pub);

		Assert.assertArrayEquals(rsaPriv.getModulus().toByteArray(), serPriv
				.getModulus().toByteArray());
		Assert.assertArrayEquals(rsaPub.getModulus().toByteArray(), serPub
				.getModulus().toByteArray());
		Assert.assertArrayEquals(rsaPriv.getModulus().toByteArray(), serPub
				.getModulus().toByteArray());
		Assert.assertArrayEquals(rsaPub.getModulus().toByteArray(), serPriv
				.getModulus().toByteArray());

		Assert.assertArrayEquals(rsaPriv.getPrivateExponent().toByteArray(),
				serPriv.getPrivateExponent().toByteArray());
		Assert.assertArrayEquals(rsaPub.getPublicExponent().toByteArray(),
				serPub.getPublicExponent().toByteArray());

		// Test a signature to be safe
		byte[] msg = new String("Hello World 110011").getBytes();
		byte[] sign1 = Cryptoutil.signature(msg, rsaPriv);
		byte[] sign2 = Cryptoutil.signature(msg, serPriv);

		Assert.assertTrue(Cryptoutil.signature_valid(sign1, msg, serPub));
		Assert.assertTrue(Cryptoutil.signature_valid(sign1, msg, rsaPub));
		Assert.assertTrue(Cryptoutil.signature_valid(sign2, msg, serPub));
		Assert.assertTrue(Cryptoutil.signature_valid(sign2, msg, rsaPub));

	}

	@Test
	public void testThatRSASerializationWillWork()
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// Does not test our functionality, just checking that this is possible

		byte[] msg = new String("Hello World").getBytes();
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();

		RSAPrivateKey priv = (RSAPrivateKey) pair.getPrivate();
		RSAPublicKey pub = (RSAPublicKey) pair.getPublic();

		byte[] sign = Cryptoutil.signature(msg, priv);

		byte[] mod = priv.getModulus().toByteArray();
		byte[] priv_b = priv.getPrivateExponent().toByteArray();
		byte[] pub_b = pub.getPublicExponent().toByteArray();

		KeyFactory fact = KeyFactory.getInstance("RSA");
		BigInteger modulus = new BigInteger(1, mod);

		BigInteger privateExponent = new BigInteger(1, priv_b);
		RSAPrivateKeySpec privks = new RSAPrivateKeySpec(modulus,
				privateExponent);
		RSAPrivateKey rsapriv = (RSAPrivateKey) fact.generatePrivate(privks);

		BigInteger publicExponent = new BigInteger(1, pub_b);
		RSAPublicKeySpec pubks = new RSAPublicKeySpec(modulus, publicExponent);
		RSAPublicKey rsapub = (RSAPublicKey) fact.generatePublic(pubks);

		boolean works = Cryptoutil.signature_valid(sign, msg, rsapub);
		Assert.assertTrue(works);
		byte[] sign2 = Cryptoutil.signature(msg, rsapriv);
		boolean works_r = Cryptoutil.signature_valid(sign2, msg, pub);
		Assert.assertTrue(works_r);
	}

}
