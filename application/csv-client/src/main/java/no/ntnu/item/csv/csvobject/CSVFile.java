package no.ntnu.item.csv.csvobject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.fileutils.FileUtils;

public class CSVFile implements CSVObject {

	private IvParameterSpec iv;
	private SecretKey secretKey;

	private Capability capability;

	private byte[] plainText;
	private byte[] cipherText;
	private byte[] hash;

	public CSVFile(File f) throws IOException {
		InputStream in = new FileInputStream(f);
		this.plainText = FileUtils.readDataBinary(in, (int) f.length());
		this.secretKey = Cryptoutil.generateSymmetricKey();
		this.capability = new CapabilityImpl(CapabilityType.RO,
				this.secretKey.getEncoded(), null, true);
		this.iv = new IvParameterSpec(Cryptoutil.nHash(
				this.secretKey.getEncoded(), 2, 16));
		this.hash = Cryptoutil.hash(this.plainText, 0);
	}

	public CSVFile(Capability capability, byte[] cipherText) {
		this.setCapability(capability);
		this.cipherText = cipherText;
	}

	@Override
	public void encrypt() {
		if (this.hash == null) {
			this.hash = Cryptoutil.hash(this.plainText, 0);
		}
		byte[] tmp = new byte[this.plainText.length + this.hash.length];
		System.arraycopy(this.plainText, 0, tmp, 0, this.plainText.length);
		System.arraycopy(this.hash, 0, tmp, this.plainText.length,
				this.hash.length);
		this.cipherText = Cryptoutil.symEncrypt(tmp, this.secretKey, this.iv);
	}

	@Override
	public void decrypt() {
		byte[] tmp = Cryptoutil.symDecrypt(this.cipherText, this.secretKey,
				this.iv);
		if (this.plainText == null) {
			this.plainText = new byte[tmp.length
					- (Cryptoutil.HASH_LENGTH / Byte.SIZE)];
		}
		if (this.hash == null) {
			this.hash = new byte[Cryptoutil.HASH_LENGTH / Byte.SIZE];
		}
		System.arraycopy(tmp, 0, this.plainText, 0, this.plainText.length);
		System.arraycopy(tmp, this.plainText.length, this.hash, 0,
				this.hash.length);
	}

	private void setSecretKey(byte[] sk) {
		SecretKeySpec sks = new SecretKeySpec(sk, Cryptoutil.SYM_CIPHER);
		this.secretKey = sks;
	}

	public void setPlainText(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		this.plainText = FileUtils.readDataBinary(in, (int) file.length());
	}

	@Override
	public Capability getCapability() {
		return this.capability;
	}

	@Override
	public void setCapability(Capability capability) {
		this.capability = capability;
		byte[] key = capability.getKey();
		this.setSecretKey(key);
		this.iv = new IvParameterSpec(Cryptoutil.nHash(key, 2, 16));
	}

	@Override
	public boolean isValid() {
		if (this.plainText == null) {
			this.decrypt();
		}
		long start = System.currentTimeMillis();
		byte[] hash = Cryptoutil.hash(this.plainText, 0);
		boolean verified = Arrays.equals(hash, this.hash);
		System.out.println("Verifying file took "
				+ (System.currentTimeMillis() - start) / 1000.0 + " seconds");
		System.out.println("File size = " + this.plainText.length / 1000.0
				+ " KB");
		return verified;
	}

	public byte[] getPlainText() {
		if (this.plainText == null) {
			decrypt();
		}
		return this.plainText;
	}

	@Override
	public byte[] getTransferArray() {
		if (this.cipherText == null) {
			encrypt();
		}
		return this.cipherText;
	}

	public byte[] getCipherText() {
		return this.cipherText;
	}

	public static CSVFile createFromByteArray(byte[] input, Capability cap) {
		CSVFile file = new CSVFile(cap, input);
		return file;

	}

}
