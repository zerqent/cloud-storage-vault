package no.ntnu.item.dictionaryattack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import de.rtner.security.auth.spi.PBKDF2Engine;
import de.rtner.security.auth.spi.PBKDF2Formatter;
import de.rtner.security.auth.spi.PBKDF2HexFormatter;
import de.rtner.security.auth.spi.PBKDF2Parameters;

public class DAMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, LongWritable> {

	private SecretKey t_key;
	private byte[] t_plain;
	private byte[] t_salt;
	private byte[] t_cipher;
	private Path[] localFiles;

	public boolean check(String word) {
		this.setKey(word);
		this.t_plain = symECBDecrypt(t_cipher, t_key);
		// 68 = char D and indicate correct decryption of first byte
		if (this.t_plain != null && this.t_plain[0] == 68) {
			String[] parts = new String(t_plain).split(":");
			if (parts.length == 4) {
				return true;
			}
		}
		return false;
	}

	public synchronized void setKey(String password) {
		PBKDF2Formatter formatter = new PBKDF2HexFormatter();
		PBKDF2Parameters param = new PBKDF2Parameters("HmacSHA1", "ISO-8859-1",
				t_salt, 1000);
		PBKDF2Engine engine = new PBKDF2Engine(param);
		param.setDerivedKey(engine.deriveKey(password, 16));
		String tmp = formatter.toString(param).split(":")[2];
		t_key = new SecretKeySpec(tmp.getBytes(), "AES");

	}

	public synchronized byte[] symECBDecrypt(byte[] cipherText, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key);
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
				System.exit(0);
			}
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public synchronized void configure(JobConf job) {
		try {
			System.out
					.println("Starting config: " + System.currentTimeMillis());
			localFiles = DistributedCache.getLocalCacheFiles(job);
			File tmp = new File(localFiles[0].toString());
			FileInputStream in = new FileInputStream(tmp);
			byte[] b = new byte[(int) tmp.length()];
			in.read(b);
			in.close();
			t_salt = new byte[8];
			t_cipher = new byte[b.length - t_salt.length];
			System.arraycopy(b, 0, t_salt, 0, t_salt.length);
			System.arraycopy(b, t_salt.length, t_cipher, 0, t_cipher.length);
			System.out.println("START: " + System.currentTimeMillis());
		} catch (IOException e) {
			System.out.println("Could not read from distributed cache!");
			e.printStackTrace();
		}

	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, LongWritable> output, Reporter reporter)
			throws IOException {
		String[] chunk = value.toString().split("\n");
		for (String word : chunk) {
			if (check(word)) {
				System.out.println("FOUND WORD: " + System.currentTimeMillis());
				output.collect(new Text(word), new LongWritable(1));
			}
		}
		System.out.println("DONE: " + System.currentTimeMillis());
	}
}
