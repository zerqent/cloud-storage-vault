package no.ntnu.item.cryptoutil;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Formatter;
import de.rtner.security.auth.spi.PBKDF2HexFormatter;
import de.rtner.security.auth.spi.PBKDF2Parameters;

public class KeyChain {

	private String password;
	private byte[] salt;
	private int iterations;
	private SecretKey key;

	// Creating key chain
	public KeyChain(String password) {
		System.out.println("KEYCHAIN CONSTRUCTOR");
		this.password = password;
		this.salt = new byte[8];
		this.iterations = 1000;

		PBKDF2Formatter formatter = new PBKDF2HexFormatter();
		SecureRandom sr;
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
			sr.nextBytes(this.salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		PBKDF2Parameters param = new PBKDF2Parameters("HmacSHA1", "ISO-8859-1",
				this.salt, this.iterations);
		PBKDF2Engine engine = new PBKDF2Engine(param);

		param.setDerivedKey(engine.deriveKey(password,
				(Cryptoutil.SYM_SIZE / Byte.SIZE)));

		String tmp = formatter.toString(param).split(":")[2];
		this.key = new SecretKeySpec(tmp.getBytes(), Cryptoutil.SYM_CIPHER);
	}

	// Creating key chain from salt
	public KeyChain(String password, byte[] salt) {
		this.password = password;
		this.iterations = 1000;

		PBKDF2Formatter formatter = new PBKDF2HexFormatter();
		if (salt == null || salt.length != 8) {
			this.salt = new byte[8];
			SecureRandom sr;
			try {
				sr = SecureRandom.getInstance("SHA1PRNG");
				sr.nextBytes(this.salt);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else {
			this.salt = salt;
		}

		PBKDF2Parameters param = new PBKDF2Parameters("HmacSHA1", "ISO-8859-1",
				this.salt, this.iterations);
		PBKDF2Engine engine = new PBKDF2Engine(param);

		param.setDerivedKey(engine.deriveKey(password,
				(Cryptoutil.SYM_SIZE / Byte.SIZE)));

		String tmp = formatter.toString(param).split(":")[2];
		this.key = new SecretKeySpec(tmp.getBytes(), Cryptoutil.SYM_CIPHER);
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
		return this.iterations;
	}
}
