package no.ntnu.item.csv.csvobject;

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
import no.ntnu.item.csv.fileutils.FileUtils;

public class CSVFile implements CSVObject {

	private IvParameterSpec iv;
	private SecretKey secretKey;

	private Capability capability;

	private byte[] plainText;
	private byte[] cipherText;

	public CSVFile(File f) throws IOException {
		InputStream in = new FileInputStream(f);
		this.plainText = FileUtils.readDataBinary(in, (int) f.length());
		this.secretKey = Cryptoutil.generateSymmetricKey();
		this.capability = new CapabilityImpl(CapabilityType.RO,
				this.secretKey.getEncoded(), null);
		this.iv = new IvParameterSpec(Cryptoutil.nHash(
				this.secretKey.getEncoded(), 2, 16));
	}

	public CSVFile(Capability capability, byte[] cipherText) {
		this.setCapability(capability);
		this.cipherText = cipherText;
	}

	public void encrypt() {
		this.cipherText = Cryptoutil.symEncrypt(this.plainText, this.secretKey,
				this.iv);
	}

	public void decrypt() {
		this.plainText = Cryptoutil.symDecrypt(this.cipherText, this.secretKey,
				this.iv);
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
		// TODO Auto-generated method stub
		return false;
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
		byte[] transfer = new byte[1 + this.cipherText.length];
		transfer[0] = 0;
		System.arraycopy(this.cipherText, 0, transfer, 1,
				this.cipherText.length);
		return transfer;
	}

	public byte[] getCipherText() {
		return this.cipherText;
	}

	public static CSVFile createFromByteArray(byte[] input, Capability cap) {

		byte[] cipherText = new byte[input.length - 1];
		System.arraycopy(input, 1, cipherText, 0, cipherText.length);
		CSVFile foo = new CSVFile(cap, cipherText);
		return foo;

	}

}
