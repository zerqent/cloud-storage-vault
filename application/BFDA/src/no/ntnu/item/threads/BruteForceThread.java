package no.ntnu.item.threads;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.bruteforce.BruteForce;
import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Parameters;

public class BruteForceThread implements Runnable {

	public Thread thread;
	private String t_word;
	private SecretKey t_key;
	private byte[] t_plain;
	private byte[] t_salt;
	private byte[] t_cipher;

	public BruteForceThread() {

	}

	public BruteForceThread(String id) {
		synchronized (BruteForce.salt) {
			t_salt = BruteForce.salt;
		}
		synchronized (BruteForce.cipher) {
			t_cipher = BruteForce.cipher;
		}
		t_key = null;
		t_plain = null;
		thread = new Thread(this, id);
		thread.start();
	}

	@Override
	public void run() {
		synchronized (BruteForce.words) {
			try {
				BruteForce.words.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (true) {
			synchronized (BruteForce.words) {
				if (BruteForce.words.size() == 0)
					break;
				t_word = new String(BruteForce.words.remove(0));
			}
			if (checkWord(t_word)) {
				BruteForce.time = (System.currentTimeMillis() - BruteForce.start) / 1000.0;
				BruteForce.printSuccess(t_word);
				BruteForce.found = true;
				break;
			}
		}
		if (!BruteForce.found && Thread.activeCount() == 2) {
			BruteForce.printFail();
		}
	}

	public boolean checkWord(String word) {
		this.setKey(word);
		t_plain = symECBDecrypt(t_cipher, t_key);
		// 68 = char D and indicate correct decryption of first byte
		if (t_plain != null && t_plain[0] == 68) {
			// If first byte is correct: look at plaintext pattern
			String[] parts = new String(t_plain).split(":");
			String[] parts2 = new String(t_plain).split("|");
			if (parts.length == 4 && parts2.length == 5) {
				return true;
			}
		}
		return false;
	}

	public void setKey(String password) {
		PBKDF2Parameters param = new PBKDF2Parameters("HmacSHA256", "UTF-8",
				t_salt, 4096);
		PBKDF2Engine engine = new PBKDF2Engine(param);
		param.setDerivedKey(engine.deriveKey(password, 16));
		t_key = new SecretKeySpec(param.getDerivedKey(), "AES");

	}

	public synchronized byte[] symECBDecrypt(byte[] cipherText, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			BruteForce.words_read++;
			return cipher.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			if (e.getMessage().equals("Illegal key size or default parameter")) {
				e.printStackTrace();
				System.out
						.println("\nERROR: Have you installed the Java(TM) Cryptography Extension (JCE) Jurisdiction Policy Files?");
				System.exit(1);
			}
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
		}
		return null;
	}

}
