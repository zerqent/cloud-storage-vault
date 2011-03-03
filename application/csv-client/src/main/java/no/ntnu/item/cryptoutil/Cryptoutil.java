package no.ntnu.item.cryptoutil;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Cryptoutil {
	
	// Hashing
	public static final String HASH_ALGORITHM = "SHA-256";
	
	// Symmetric Cipher
	public static final String SYM_CIPHER = "AES";
	public static final String SYM_PADDING = "PKCS5Padding";
	public static final String SYM_MODE = "CBC";
	public static final int SYM_SIZE = 128;
	
	// Asymmetric Cipher
	public static final String ASYM_CIPHER = "RSA";
	public static final int ASYM_SIZE = 1024;
	
	/**
	 * Hash the input with the configured hash algorithm
	 * @param input
	 * @param truncate_to
	 * @return
	 */
	public static byte[] hash(byte[] input, int truncate_to) {
		byte result[] = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance(Cryptoutil.HASH_ALGORITHM);
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
	 * @param input
	 * @param n
	 * @param truncate_to
	 * @return
	 */
	public static byte[] nHash(byte[] input, int n, int truncate_to) {
		if (n<1) {
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
	 * @return
	 */
	public static SecretKey generateSymmetricKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance(Cryptoutil.SYM_CIPHER);
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
	 * @return
	 */
	public static KeyPair generateAsymmetricKeys() {
		KeyPairGenerator keygen;
		try {
			keygen = KeyPairGenerator.getInstance(Cryptoutil.ASYM_CIPHER);
			keygen.initialize(Cryptoutil.ASYM_SIZE);
			return keygen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] symEncrypt(byte[] plainText, SecretKey key, IvParameterSpec iv) {
		try {
			Cipher cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER+"/"+Cryptoutil.SYM_MODE+"/"+Cryptoutil.SYM_PADDING);
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
	
	public static byte[] symDecrypt(byte[] plainText, SecretKey key, IvParameterSpec iv) {
		try {
			Cipher cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER+"/"+Cryptoutil.SYM_MODE+"/"+Cryptoutil.SYM_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
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
	
}