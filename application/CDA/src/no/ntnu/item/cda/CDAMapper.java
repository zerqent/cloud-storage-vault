package no.ntnu.item.cda;

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

public class CDAMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, LongWritable> {

	public static byte[] salt;
	public static byte[] cipher;
	public static String password;

	private int THREADS;
	private int WORDS_PER_THREAD;
	private Path[] localFiles;
	public static DictionaryThread[] dictionary_threads;

	private SecretKey key;
	private byte[] plain;

	@Override
	public synchronized void configure(JobConf conf) {
		try {
			System.out.println("Starting MAPPER config: "
					+ System.currentTimeMillis());
			THREADS = conf.getInt("THREADS", 1);
			WORDS_PER_THREAD = conf.getInt("PASSWORDS_PER_LINE", 1) / THREADS;
			dictionary_threads = new DictionaryThread[THREADS];
			password = "";
			localFiles = DistributedCache.getLocalCacheFiles(conf);
			File tmp = new File(localFiles[0].toString());
			FileInputStream in = new FileInputStream(tmp);
			byte[] b = new byte[(int) tmp.length()];
			in.read(b);
			in.close();
			salt = new byte[16];
			cipher = new byte[b.length - salt.length];
			System.arraycopy(b, 0, salt, 0, salt.length);
			System.arraycopy(b, salt.length, cipher, 0, cipher.length);
			System.out.println("MAPPER START: " + System.currentTimeMillis());

		} catch (IOException e) {
			System.out.println("Could not read from distributed cache!");
			e.printStackTrace();
		}

	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, LongWritable> output, Reporter reporter)
			throws IOException {

		String[] line = value.toString().split(" ");
		String[] line_chunk = new String[WORDS_PER_THREAD];

		// Create threads to decrypt words in chunk
		for (int i = 0, c = 0; i < (THREADS * WORDS_PER_THREAD)
				&& i < line.length; i += WORDS_PER_THREAD, c++) {
			if (line.length - i >= WORDS_PER_THREAD) {
				System.arraycopy(line, i, line_chunk, 0, WORDS_PER_THREAD);
			} else {
				System.arraycopy(line, i, line_chunk, 0, line.length - i);
			}
			dictionary_threads[c] = new DictionaryThread("dict" + i, line_chunk);
		}

		// If the number of passwords for this line is not a multiple of the
		// number of threads in use, there will be a set of remaining passwords.
		// These passwords are checked below. This will
		// not be activated if an optimized dictionary is used!
		if (line.length > THREADS * WORDS_PER_THREAD) {
			for (int i = 0; i < (line.length - THREADS); i++) {
				if (check(line[THREADS + i])) {
					System.out.println("FOUND PASSWORD: "
							+ System.currentTimeMillis());
					CDAMapper.password = line[THREADS + i];
					break;
				}
			}
		}

		// Wait for all threads to finish
		for (DictionaryThread dt : dictionary_threads) {
			try {
				dt.thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!password.equals("")) {
			output.collect(new Text("Password is [ " + password
					+ " ]. Found at"),
					new LongWritable(System.currentTimeMillis()));
		}
	}

	public boolean check(String word) {
		this.setKey(word);
		this.plain = symECBDecrypt(cipher, key);

		// 68 = char D and indicate correct decryption of first byte
		if (this.plain != null && this.plain[0] == 68) {
			String[] parts = new String(plain).split(":");
			if (parts.length == 4)
				return true;
		}
		return false;
	}

	public void setKey(String password) {
		PBKDF2Formatter formatter = new PBKDF2HexFormatter();
		PBKDF2Parameters param = new PBKDF2Parameters("HmacSHA1", "ISO-8859-1",
				salt, 1000);
		PBKDF2Engine engine = new PBKDF2Engine(param);
		param.setDerivedKey(engine.deriveKey(password, 16));
		String tmp = formatter.toString(param).split(":")[2];
		key = new SecretKeySpec(tmp.getBytes(), "AES");
	}

	public byte[] symECBDecrypt(byte[] cipherText, SecretKey key) {
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
				System.out
						.println("\nERROR: Have you installed the Java(TM) Cryptography Extension (JCE) Jurisdiction Policy Files?");
				e.printStackTrace();
			}
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}

}
