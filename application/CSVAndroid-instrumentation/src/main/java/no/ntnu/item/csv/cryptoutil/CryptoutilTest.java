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
import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.testutils.ArrayComparison;
import android.test.ActivityInstrumentationTestCase2;

public class CryptoutilTest extends
		ActivityInstrumentationTestCase2<CSVActivity> {

	public CryptoutilTest() {
		super("no.ntnu.item.csv", CSVActivity.class);
	}

	public void testNormalHashing() throws Exception {
		byte[] test = { 50, 100, 34, 56, 78 };

		byte[] res = Cryptoutil.hash(test, 0);
		assertEquals(32, res.length);

		MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);
		md.update(test);
		byte[] tmp = md.digest();
		md.reset();
		md.update(tmp);

		// Assert.assertArrayEquals(md.digest(), res);
		assertTrue(ArrayComparison.arraysAreEqual(md.digest(), res));
	}

	public void testTruncatedHashing() throws Exception {
		byte[] test = { 50, 100, 34, 56, 78 };

		byte[] res = Cryptoutil.hash(test, 16);
		assertEquals(16, res.length);

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

		assertTrue(ArrayComparison.arraysAreEqual(trunc, res));
	}

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
			assertTrue(ArrayComparison.arraysAreEqual(tmp,
					Cryptoutil.nHash(msg, i, 0)));
			store = tmp;
		}

	}

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
				assertEquals(truncate_to, tmp.length);
				assertTrue(ArrayComparison.arraysAreEqual(store, tmp));
			}
		}
	}

	public void testSigning() {
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		byte[] msg = { 50, 100, 34, 56, 78, 12, 56, 13, 16, 68, 44, 45, 76, 45,
				34, 12 };
		byte[] hashed = Cryptoutil.hash(msg, -1);
		byte[] sign = Cryptoutil.signature(hashed, pair.getPrivate());
		assertTrue(Cryptoutil.signature_valid(sign, hashed, pair.getPublic()));

		KeyPair pair2 = Cryptoutil.generateAsymmetricKeys();
		assertFalse(Cryptoutil.signature_valid(sign, hashed, pair2.getPublic()));
	}

	public void testRSAKeyGeneration() {
		int TEST_AMOUNT = 2;

		// We need keys to be of a static size //TODO: Figure out why
		// PrivateExponent.length is sometimes 129
		for (int i = 0; i < TEST_AMOUNT; i++) {
			KeyPair pair = Cryptoutil.generateAsymmetricKeys();
			RSAPrivateKey priv = (RSAPrivateKey) pair.getPrivate();
			RSAPublicKey pub = (RSAPublicKey) pair.getPublic();

			assertEquals(129, priv.getModulus().toByteArray().length);
			assertEquals(129, pub.getModulus().toByteArray().length);
			assertEquals(128, priv.getPrivateExponent().toByteArray().length);
			assertEquals(3, pub.getPublicExponent().toByteArray().length);
		}
	}

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

		assertTrue(ArrayComparison.arraysAreEqual(rsaPriv.getModulus()
				.toByteArray(), serPriv.getModulus().toByteArray()));
		assertTrue(ArrayComparison.arraysAreEqual(rsaPub.getModulus()
				.toByteArray(), serPub.getModulus().toByteArray()));
		assertTrue(ArrayComparison.arraysAreEqual(rsaPriv.getModulus()
				.toByteArray(), serPub.getModulus().toByteArray()));
		assertTrue(ArrayComparison.arraysAreEqual(rsaPub.getModulus()
				.toByteArray(), serPriv.getModulus().toByteArray()));

		assertTrue(ArrayComparison.arraysAreEqual(rsaPriv.getPrivateExponent()
				.toByteArray(), serPriv.getPrivateExponent().toByteArray()));
		assertTrue(ArrayComparison.arraysAreEqual(rsaPub.getPublicExponent()
				.toByteArray(), serPub.getPublicExponent().toByteArray()));

		// Test a signature to be safe
		byte[] msg = new String("Hello World 110011").getBytes();
		byte[] sign1 = Cryptoutil.signature(msg, rsaPriv);
		byte[] sign2 = Cryptoutil.signature(msg, serPriv);

		assertTrue(Cryptoutil.signature_valid(sign1, msg, serPub));
		assertTrue(Cryptoutil.signature_valid(sign1, msg, rsaPub));
		assertTrue(Cryptoutil.signature_valid(sign2, msg, serPub));
		assertTrue(Cryptoutil.signature_valid(sign2, msg, rsaPub));

	}

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
		assertTrue(works);
		byte[] sign2 = Cryptoutil.signature(msg, rsapriv);
		boolean works_r = Cryptoutil.signature_valid(sign2, msg, pub);
		assertTrue(works_r);
	}

}
