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

public class CSVFileImpl extends CSVFileFacade {

	private IvParameterSpec iv;
	private SecretKey secretKey;

	private Capability capability;

	private byte[] plainText;
	private byte[] cipherText;

	public CSVFileImpl(File f) throws IOException {
		super(f);
		this.secretKey = Cryptoutil.generateSymmetricKey();
		this.capability = new CapabilityImpl(CapabilityType.RO,
				this.secretKey.getEncoded(), null);
		this.iv = new IvParameterSpec(Cryptoutil.nHash(
				this.secretKey.getEncoded(), 2, 16));
	}

	public CSVFileImpl(Capability capability, byte[] cipherText) {
		super(capability, cipherText);
	}

	@Override
	public void encrypt() {
		this.cipherText = Cryptoutil.symEncrypt(this.plainText, this.secretKey,
				this.iv);
	}

	@Override
	public void decrypt() {
		this.plainText = Cryptoutil.symDecrypt(this.cipherText, this.secretKey,
				this.iv);
	}

	private void setSecretKey(byte[] sk) {
		SecretKeySpec sks = new SecretKeySpec(sk, Cryptoutil.SYM_CIPHER);
		this.secretKey = sks;
	}

	@Override
	public void setPlainText(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		this.plainText = CSVFileFacade.readDataBinary(in, (int) file.length());
	}

	@Override
	public Capability getCapability() {
		return this.capability;
	}

	@Override
	public void setCapability(Capability capability) {
		this.capability = capability;
		this.setSecretKey(capability.getKey());
		this.iv = new IvParameterSpec(Cryptoutil.nHash(
				this.capability.getKey(), 2, 16));
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] getCipherText() {
		return this.cipherText;
	}

	@Override
	protected void setCipherText(byte[] cipherText) {
		this.cipherText = cipherText;
	}

	@Override
	public byte[] getPlainText() {
		return this.plainText;
	}

	@Override
	public byte[] getTransferArray() {
		byte[] transfer = new byte[1 + this.cipherText.length];
		transfer[0] = 0;
		System.arraycopy(this.cipherText, 0, transfer, 1,
				this.cipherText.length);
		return transfer;
	}

	public static CSVFileImpl createFromByteArray(byte[] input, Capability cap) {

		byte[] cipherText = new byte[input.length - 1];
		System.arraycopy(input, 1, cipherText, 0, cipherText.length);
		CSVFileImpl foo = new CSVFileImpl(cap, cipherText);
		return foo;

	}

}
