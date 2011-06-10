package no.ntnu.item.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.fileutils.FileUtils;
import android.os.AsyncTask;

public class CryptoSpeed extends AsyncTask<Void, Void, Long> {

	private SecretKey sk;
	private byte[] iv;
	private byte[] data;
	private Cipher cipher;
	private byte[] ciphertext;
	private long size;

	@Override
	protected void onPreExecute() {
		this.sk = Cryptoutil.generateSymmetricKey();
		this.iv = Cryptoutil.generateIV();
		try {
			File file = new File("/sdcard/test.mp3");
			this.size = file.length();

			long before = System.currentTimeMillis();
			this.data = FileUtils.readDataBinary(new FileInputStream(file),
					(int) this.size);
			long after = System.currentTimeMillis();
			long diff = after - before;
			System.out.println("CSV: READ-SIZE: " + this.size);
			System.out.println("CSV: READ-TIME: " + diff);

			this.cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ Cryptoutil.SYM_MODE + "/" + Cryptoutil.SYM_PADDING);
			this.cipher.init(Cipher.ENCRYPT_MODE, this.sk, new IvParameterSpec(
					this.iv));
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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("CSV: STARTING ENCRYPTION");
	}

	@Override
	protected Long doInBackground(Void... params) {

		try {
			long before = System.currentTimeMillis();
			this.ciphertext = this.cipher.doFinal(this.data);
			long after = System.currentTimeMillis();
			long diff = after - before;
			this.data = null;

			System.out.println("CSV: E-SIZE: " + this.size);
			System.out.println("CSV: E-TIME: " + diff);

			this.cipher = Cipher.getInstance(Cryptoutil.SYM_CIPHER + "/"
					+ Cryptoutil.SYM_MODE + "/" + Cryptoutil.SYM_PADDING);
			this.cipher.init(Cipher.DECRYPT_MODE, this.sk, new IvParameterSpec(
					this.iv));

			before = System.currentTimeMillis();
			this.data = this.cipher.doFinal(this.ciphertext);
			after = System.currentTimeMillis();
			diff = after - before;

			System.out.println("CSV: D-SIZE: " + this.size);
			System.out.println("CSV: D-TIME: " + diff);

			// Let's hash a file
			before = System.currentTimeMillis();
			// this.data = this.cipher.doFinal(this.ciphertext);
			Cryptoutil.nHash(this.data, 2, -1);
			after = System.currentTimeMillis();
			diff = after - before;

			System.out.println("CSV: H-SIZE: " + this.size);
			System.out.println("CSV: H-TIME: " + diff);

		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		}
		return null;
	}

	@Override
	protected void onPostExecute(Long result) {
		return;
	}

}
