package no.ntnu.item.csv.csvobject.impl;

import java.security.KeyPair;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

public class CSVFolderImpl extends CSVFolderFacade{

	private byte[] pubkey;
	private byte[] privkey;

	private Capability capability;
	private Map<String, Capability> contents; 

	private byte[] ciphertext;
	private byte[] plainText;
	private byte[] iv;

	private byte[] signature;

	public CSVFolderImpl() {
		super();
		generateKeys();
	}

	public CSVFolderImpl(Capability capability, byte[] cipherText, byte[] pubkey, byte[] iv, byte[] signature) {
		super(capability, cipherText, pubkey, iv, signature);
	}

	private void generateKeys() {

		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		this.pubkey = pair.getPublic().getEncoded();
		this.privkey = pair.getPrivate().getEncoded();

		byte[] write = Cryptoutil.hash(this.privkey, 16);
		//byte[] read = Cryptoutil.hash(write, 16);
		byte[] verify = Cryptoutil.hash(this.pubkey, 16);

		Capability writecap = new CapabilityImpl(CapabilityType.RW, write, verify);	
		this.capability = writecap;	
	}

	private void sign() {
		byte[] hash = Cryptoutil.hash(this.ciphertext, -1);
		this.signature = Cryptoutil.signature(hash, this.privkey);
	}

	@Override
	public void encrypt() {
		byte[] read;
		if (this.capability.getType() == CapabilityType.RW) {
			read = Cryptoutil.hash(this.capability.getKey(), 16);
		} else {
			read = this.capability.getKey();
		}
		this.createPlainText();
		SecretKeySpec sks = new SecretKeySpec(read, Cryptoutil.SYM_CIPHER);
		this.ciphertext = Cryptoutil.symEncrypt(this.plainText, sks, new IvParameterSpec(Cryptoutil.generateIV()));
		sign();
	}

	@Override
	public void decrypt() {
		byte[] read;
		if (this.capability.getType() == CapabilityType.RW) {
			read = Cryptoutil.hash(this.capability.getKey(), 16);
		} else {
			read = this.capability.getKey();
		}
		SecretKeySpec sks = new SecretKeySpec(read, Cryptoutil.SYM_CIPHER);
		this.plainText = Cryptoutil.symDecrypt(this.ciphertext, sks, new IvParameterSpec(this.iv));
		this.createContentsFromPlainText();
	}

	@Override
	public boolean isValid() {
		byte[] hash = Cryptoutil.hash(this.ciphertext, -1);
		return Cryptoutil.signature_valid(this.signature, hash, this.pubkey);
	}

	@Override
	public byte[] getCipherText() {
		return this.ciphertext;
	}

	@Override
	public Capability getCapability() {
		return this.capability;
	}

	@Override
	public void setCapability(Capability capability) {
		this.capability = capability;
	}

	@Override
	public Map<String, Capability> getContents() {
		return this.contents;
	}

	@Override
	protected void setPlainText(byte[] plainText) {
		this.plainText = plainText;
	}

	@Override
	protected void setCipherText(byte[] cipherText) {
		this.ciphertext = cipherText;
	}

	@Override
	protected byte[] getPlainText() {
		return this.plainText;
	}

	@Override
	protected void setPubKey(byte[] pubKey) {
		this.pubkey = pubKey;
	}

	@Override
	protected void setIV(byte[] iv) {
		this.iv = iv;
	}

	@Override
	protected void setContents(Map<String, Capability> contents) {
		this.contents = contents;
	}

	@Override
	protected void setSignature(byte[] signature) {
		this.signature = signature;

	}

	@Override
	public void addContent(String alias, Capability capability) {
		this.contents.put(alias, capability);

	}

}
