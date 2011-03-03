package no.ntnu.item.csv.csvobject.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;


public class CSVFileImplHelper {

	private IvParameterSpec iv;
	private SecretKey secretKey;

	private Capability capability;

	private byte[] plainText = null;
	private byte[] cipherText = null;

	private byte[] cipherTextDigest = null;
	private byte[] plainTextDigest = null;

	public CSVFileImplHelper() {
		this.iv = null;
		this.secretKey = Cryptoutil.generateSymmetricKey();
		this.capability = new CapabilityImpl(CapabilityType.RO, this.secretKey.getEncoded(),null);
		this.setIV(Cryptoutil.nHash(this.secretKey.getEncoded(), 2, 16));
	}

	public CSVFileImplHelper(Capability capability, byte[] cipherText) {
		this.capability = capability;
		this.cipherText = cipherText;
		this.setSecretKey(this.capability.getKey());
		this.setIV(Cryptoutil.nHash(this.capability.getKey(), 2, 16));
	}

	public boolean isPlainTextReady() {
		return this.plainText != null;
	}

	public boolean isCipherTextReady() {
		return this.cipherText != null;
	}

	public void encrypt() {
		this.cipherText = Cryptoutil.symEncrypt(this.plainText, this.secretKey, this.iv);
	}

	public void decrypt() {
		this.plainText = Cryptoutil.symDecrypt(this.cipherText, this.secretKey, this.iv);
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
		SecretKeySpec sks = new SecretKeySpec(sk, Cryptoutil.SYM_CIPHER);
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
			this.cipherTextDigest = Cryptoutil.hash(this.cipherText, -1);
		}
		return this.cipherTextDigest;
	}

	public byte[] getPlainTextDigest() {
		if(this.plainTextDigest == null && !this.isPlainTextReady()) {
			return null;
		} else if (this.plainTextDigest == null){
			this.plainTextDigest = Cryptoutil.hash(this.plainText, -1);
		}
		return this.plainTextDigest;
	}

	public void setPlainText(File file) throws IOException{
		InputStream in = new FileInputStream(file);
		this.setPlainText(readDataBinary(in, (int)file.length()));
	}

	public Capability getCapability() {
		return this.capability;
	}

	public void setCapability(Capability capability) {
		this.capability = capability;
		this.setSecretKey(capability.getKey());
		this.setIV(Cryptoutil.nHash(this.capability.getKey(), 2, 16));
	}

	public static byte[] readDataBinary(InputStream in, int filelength) throws IOException {
		//TODO: 32-bit warning right here..
		byte[] bytes = new byte[filelength];
		int offset = 0;
		int numRead = 0;
		
		while (offset < bytes.length && (numRead=in.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not read entire file");
		}
		//this.contentLength = filelength;
		//this.setData(bytes);
		return bytes;
	}

}
