package no.ntnu.item.csv.csvobject.impl;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.csv.capability.CSVKey;
import no.ntnu.item.csv.capability.Capability;


public class CSVFileImplHelper {

	private static final String transformation = "AES/CBC/PKCS5Padding";
	private static final int keySize = 128;
	private static final String cipherName = "AES";
	private static final String digestName = "SHA-256";
	
	private IvParameterSpec iv;
	private Cipher cipher;
	private SecretKey secretKey;
	
	private CSVKey csvkey;
	private Capability capability;
	
	private byte[] plainText = null;
	private byte[] cipherText = null;
	
	private MessageDigest cipherTextDigest = null;
	private MessageDigest plainTextDigest = null;
	
	public CSVFileImplHelper() {
		this.iv = null;
		try {
			this.cipher = Cipher.getInstance(transformation);
			//
			KeyGenerator keygen = KeyGenerator.getInstance(cipherName);
			keygen.init(keySize);
			this.secretKey = keygen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public boolean isPlainTextReady() {
		return this.plainText != null;
	}
	
	public boolean isCipherTextReady() {
		return this.cipherText != null;
	}

	public void encrypt() {
		if(this.isCipherTextReady()) {
			return;
		}
		try {
			this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
			this.cipherText = this.cipher.doFinal(this.plainText);
			this.iv = new IvParameterSpec(this.cipher.getIV());
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
		
	}

	public void decrypt() {
		if(this.isPlainTextReady()) {
			return;
		}
		try {
			this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey, this.iv);
			this.plainText = this.cipher.doFinal(this.cipherText);
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
		
	}

	public void verify() {
		// TODO Auto-generated method stub
		
	}

	public byte[] getPlainText() {
		return this.plainText;
	}

	public byte[] getCipherText() {
		return this.cipherText;
	}

	public void setPlainText(byte[] data) {
		this.plainText = data;
		
	}

	public void setCipherText(byte[] data) {
		this.cipherText = data;
		
	}

	public void setIV(byte[] iv) {
		this.iv = new IvParameterSpec(iv);
		
	}

	public void setSecretKey(byte[] sk) {
		SecretKeySpec sks = new SecretKeySpec(sk, CSVFileImplHelper.cipherName);
		this.secretKey = sks;
	}

	public byte[] getIV() {
		if (this.iv != null) {
			return this.iv.getIV();
		}
		return null;
	}

	public byte[] getSecretKey() {
		if (this.secretKey != null) {
			return this.secretKey.getEncoded();	
		}
		return null;
	}

	public byte[] getCipherTextDigest() {
		if(this.cipherTextDigest == null && !this.isCipherTextReady()) {
			return null;
		} else if (this.cipherTextDigest == null){
			try {
				this.cipherTextDigest = MessageDigest.getInstance(digestName);
				this.cipherTextDigest.update(this.cipherText);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.cipherTextDigest.digest();
	}

	public byte[] getPlainTextDigest() {
		if(this.plainTextDigest == null && !this.isPlainTextReady()) {
			return null;
		} else if (this.plainTextDigest == null){
			try {
				this.plainTextDigest = MessageDigest.getInstance(digestName);
				this.plainTextDigest.update(this.plainText);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.plainTextDigest.digest();
	}

	public void setPlainText(File f) {
		// TODO Auto-generated method stub
		
	}

}
