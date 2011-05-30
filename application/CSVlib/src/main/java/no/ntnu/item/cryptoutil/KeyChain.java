package no.ntnu.item.cryptoutil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Parameters;

public class KeyChain {

	private String password;
	private byte[] salt;
	private SecretKey key;

	public static final int SALT_SIZE = Cryptoutil.SYM_SIZE;
	public static final String HASH_ALG = Cryptoutil.HMAC_ALG;
	public static final String RANDOM_GENERATOR = "SHA1PRNG";
	public static final int ITERATION_COUNT = 4096;
	public static final String ENCODING = "UTF-8";

	/**
	 * Generate a new PBKDF2 key
	 * 
	 * @param password
	 */
	public KeyChain(String password) {
		assert password != null;
		this.password = password;
		this.salt = new byte[SALT_SIZE / Byte.SIZE];

		SecureRandom sr;
		try {
			sr = SecureRandom.getInstance(RANDOM_GENERATOR);
			sr.nextBytes(this.salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		common();
	}

	/**
	 * Generate existing PBKDF2 key
	 * 
	 * @param password
	 * @param salt
	 */
	public KeyChain(String password, byte[] salt) {
		assert salt != null;
		assert password != null;

		this.password = password;
		this.salt = salt;

		common();
	}

	private void common() {
		PBKDF2Parameters param = new PBKDF2Parameters(HASH_ALG, ENCODING,
				this.salt, ITERATION_COUNT);
		PBKDF2Engine engine = new PBKDF2Engine(param);

		param.setDerivedKey(engine.deriveKey(password,
				(Cryptoutil.SYM_SIZE / Byte.SIZE)));

		byte tmp[] = param.getDerivedKey();
		this.key = new SecretKeySpec(tmp, Cryptoutil.SYM_CIPHER);
	}

	public SecretKey getKey() {
		return this.key;
	}

	public String getPassword() {
		return this.password;
	}

	public byte[] getSalt() {
		return this.salt;
	}

	public int getIterations() {
		return ITERATION_COUNT;
	}
}
