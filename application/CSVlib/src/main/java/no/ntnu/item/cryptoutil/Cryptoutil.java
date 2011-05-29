package no.ntnu.item.cryptoutil;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptoutil {

	// Hashing
	public static final String HASH_ALGORITHM = "SHA-256";
	public static final int HASH_LENGTH = 256;

	// Symmetric Cipher
	public static final String SYM_CIPHER = "AES";
	public static final String SYM_PADDING = "PKCS5Padding"; // TODO
	public static final String SYM_MODE = "CBC";
	public static final int SYM_SIZE = 128;

	// Asymmetric Cipher
	public static final String ASYM_CIPHER = "RSA";
	// public static final String ASYM_PADDING = "PKCS1Padding"; // TODO
	// public static final String ASYM_MODE = "ECB";
	public static final int ASYM_SIZE = 2048;

	// Signatures
	public static final String SIGN_ALG = "SHA256withRSA";

	// HMAC
	public static final String HMAC_ALG = "HmacSHA256";

	/**
	 * Hash the input with the configured hash algorithm
	 * 
	 * @param input
	 * @param truncate_to
	 * @return
	 */
	public static byte[] hash(byte[] input, int truncate_to) {
		byte result[] = null;

		try {
			MessageDigest md = MessageDigest
					.getInstance(Cryptoutil.HASH_ALGORITHM);
			md.update(input);
			result = md.digest();
			md.reset();
			md.update(result);
			result = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// Should already be tested
		}

		if (truncate_to > 0) {
			byte[] tmp = new byte[truncate_to];
			System.arraycopy(result, 0, tmp, 0, truncate_to);
			result = tmp;
		}
		return result;
	}

	public static byte[] singlehash(byte[] input, int truncate_to) {
		byte result[] = null;

		try {
			MessageDigest md = MessageDigest
					.getInstance(Cryptoutil.HASH_ALGORITHM);
			md.update(input);
			result = md.digest();
		} catch (NoSuchAlgorithmException e) {
			// Should already be tested
		}

		if (truncate_to > 0) {
			byte[] tmp = new byte[truncate_to];
			System.arraycopy(result, 0, tmp, 0, truncate_to);
			result = tmp;
		}
		return result;
	}

	/**
	 * Hash the input n number of times, output is truncated on each iteration
	 * 
	 * @param input
	 * @param n
	 * @param truncate_to
	 * @return
	 */
	public static byte[] nHash(byte[] input, int n, int truncate_to) {
		if (n < 1) {
			return null;
		}
		byte[] result = input;

		for (int i = 0; i < n; i++) {
			result = hash(result, truncate_to);
		}
		return result;
	}

	/**
	 * Generate a Symmetric key used for encryption
	 * 
	 * @return
	 */
	public static SecretKey generateSymmetricKey() {
		try {
			KeyGenerator keygen = KeyGenerator
					.getInstance(Cryptoutil.SYM_CIPHER);
			keygen.init(Cryptoutil.SYM_SIZE);
			return keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Generate a asymmetric key pair.
	 * 
	 * @return
	 */
	public static KeyPair generateAsymmetricKeys() {
		KeyPairGenerator keygen;
		try {
			boolean done = false;
			KeyPair pair = null;

			// while (!done) {
			// // Private exponent is sometimes 129, we always want it to be
			// // 128
			keygen = KeyPairGenerator.getInstance(Cryptoutil.ASYM_CIPHER);
			keygen.initialize(Cryptoutil.ASYM_SIZE);
			pair = keygen.generateKeyPair();
			// RSAPrivateKey priv = (RSAPrivateKey) pair.getPrivate();
			//
			// byte[] privExp = priv.getPrivateExponent().toByteArray();
			//
			// if (priv.getPrivateExponent().toByteArray().length ==
			// Cryptoutil.ASYM_SIZE / 8) {
			// System.out.print("foo");
			// done = true;
			// } else {
			// System.out.print("foo");
			// }
			//
			// }
			return pair;
			// return keygen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] symEncrypt(byte[] plainText, SecretKey key,
			IvParameterSpec iv) {
		try {
			Cipher cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ Cryptoutil.SYM_MODE + "/" + Cryptoutil.SYM_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			return cipher.doFinal(plainText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] symECBEncrypt(byte[] plainText, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ "ECB" + "/" + Cryptoutil.SYM_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(plainText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] symECBDecrypt(byte[] cipherText, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ "ECB" + "/" + Cryptoutil.SYM_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] symDecrypt(byte[] cipherText, SecretKey key,
			IvParameterSpec iv) {
		try {
			Cipher cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ Cryptoutil.SYM_MODE + "/" + Cryptoutil.SYM_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			return cipher.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] generateIV() {
		try {
			Cipher cip = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ Cryptoutil.SYM_MODE + "/" + Cryptoutil.SYM_PADDING);
			cip.init(Cipher.ENCRYPT_MODE, generateSymmetricKey());
			return cip.getIV();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] signature(byte[] data, PrivateKey privateKey) {

		try {
			Signature sign = Signature.getInstance(SIGN_ALG);
			sign.initSign(privateKey);
			sign.update(data);
			return sign.sign();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static boolean signature_valid(byte[] signature, byte[] data,
			PublicKey pubKey) {
		try {
			Signature sign = Signature.getInstance(SIGN_ALG);
			sign.initVerify(pubKey);
			sign.update(data);
			return sign.verify(signature);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static byte[] hmac(byte[] msg, byte[] key) {
		try {
			Mac mac = Mac.getInstance(HMAC_ALG);
			SecretKeySpec sks = new SecretKeySpec(key, Cryptoutil.SYM_CIPHER);
			mac.init(sks);
			return mac.doFinal(msg);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] serializePublicKey(RSAPublicKey pubKey) {
		byte[] mod = pubKey.getModulus().toByteArray();
		byte[] pubexp = pubKey.getPublicExponent().toByteArray();
		byte[] all = new byte[mod.length + pubexp.length];
		System.arraycopy(mod, 0, all, 0, mod.length);
		System.arraycopy(pubexp, 0, all, mod.length, pubexp.length);
		return all;
	}

	public static PublicKey createRSAPublicKey(byte key[]) {
		// First comes modulus (129), then comes public exponent (3)
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			byte[] mod = new byte[Cryptoutil.ASYM_SIZE / 8 + 1];
			byte[] publicK = new byte[3];
			System.arraycopy(key, 0, mod, 0, mod.length);
			System.arraycopy(key, mod.length, publicK, 0, publicK.length);

			BigInteger modulus = new BigInteger(1, mod);
			BigInteger publicExponent = new BigInteger(1, publicK);

			RSAPublicKeySpec pubks = new RSAPublicKeySpec(modulus,
					publicExponent);
			return fact.generatePublic(pubks);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static byte[] serializePrivateKey(RSAPrivateKey privKey) {
		byte[] mod = privKey.getModulus().toByteArray();
		byte[] pubexp = privKey.getPrivateExponent().toByteArray();
		if (pubexp.length > Cryptoutil.ASYM_SIZE / 8) {
			byte[] tmp = new byte[pubexp.length - 1];
			System.arraycopy(pubexp, 1, tmp, 0, tmp.length);
			pubexp = tmp;
		}

		byte[] all = new byte[mod.length + pubexp.length];
		System.arraycopy(mod, 0, all, 0, mod.length);
		System.arraycopy(pubexp, 0, all, mod.length, pubexp.length);
		return all;
	}

	public static PrivateKey createRSAPrivateKey(byte key[]) {
		// First comes modulus , then comes private exponent
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			byte[] mod = new byte[ASYM_SIZE / 8 + 1];
			byte[] privateK = new byte[ASYM_SIZE / 8];
			System.arraycopy(key, 0, mod, 0, mod.length);
			System.arraycopy(key, mod.length, privateK, 0, privateK.length);

			BigInteger modulus = new BigInteger(1, mod);
			BigInteger privateExponent = new BigInteger(1, privateK);

			RSAPrivateKeySpec privKs = new RSAPrivateKeySpec(modulus,
					privateExponent);
			return fact.generatePrivate(privKs);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
