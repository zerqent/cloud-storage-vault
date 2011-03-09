package no.ntnu.item.csv.csvobject.impl;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

public class CSVFolderImpl extends CSVFolderFacade{

	private PublicKey pubkey;
	private PrivateKey privkey;
	private byte[] encPrivKey;

	private Capability capability;
	private Map<String, Capability> contents; 

	private byte[] ciphertext;
	private byte[] plainText;
	private byte[] iv;

	private byte[] signature;

	public CSVFolderImpl() {
		super();
		generateKeys();
		this.contents = new HashMap<String, Capability>();
	}

	public CSVFolderImpl(Capability capability, byte[] cipherText, byte[] pubkey, byte[] iv, byte[] signature) {
		super(capability, cipherText, pubkey, iv, signature);
		this.decrypt();
	}

	private void generateKeys() {

		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		this.pubkey = pair.getPublic();
		this.privkey = pair.getPrivate();

		byte[] write = Cryptoutil.hash(this.privkey.getEncoded(), 16);
		//byte[] read = Cryptoutil.hash(write, 16);
		RSAPublicKey pub = (RSAPublicKey)this.pubkey;
		byte[] mod = pub.getModulus().toByteArray();
		byte[] pubexp = pub.getPublicExponent().toByteArray();
		byte[] to_hash = new byte[mod.length + pubexp.length];
		System.arraycopy(mod, 0, to_hash, 0, mod.length);
		System.arraycopy(pubexp, 0, to_hash, mod.length, pubexp.length);
		
		byte[] verify = Cryptoutil.hash(to_hash, 16);

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
		this.iv = Cryptoutil.generateIV();
		this.ciphertext = Cryptoutil.symEncrypt(this.plainText, sks, new IvParameterSpec(this.iv));
		sign();
		this.encPrivKey = Cryptoutil.symECBEncrypt(Cryptoutil.serializePrivateKey((RSAPrivateKey)this.privkey), new SecretKeySpec(this.capability.getKey(), Cryptoutil.SYM_CIPHER));
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
		this.pubkey = Cryptoutil.createRSAPublicKey(pubKey);		
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

	
	protected byte[] getIV() {
		return this.iv;
	}
	
	protected byte[] getPubKey() {
		return Cryptoutil.serializePublicKey((RSAPublicKey)this.pubkey);
	}
	
	@Override
	public byte[] getTransferArray() {
		if (this.ciphertext == null || this.signature == null) {
			this.encrypt();
		}
		
		byte[] pub = Cryptoutil.serializePublicKey((RSAPublicKey)this.pubkey);
		
		byte[] transfer = new byte[1+pub.length + this.signature.length + this.iv.length + this.encPrivKey.length + this.ciphertext.length];
		transfer[0] = 1;
		System.out.println(this.encPrivKey.length);
		// TODO: Make more generic

		System.arraycopy(pub, 0, transfer, 1, pub.length);
		System.arraycopy(this.signature, 0, transfer, 1+pub.length, this.signature.length);
		System.arraycopy(this.iv, 0, transfer, 1+pub.length + this.signature.length , this.iv.length);
		System.arraycopy(this.encPrivKey, 0, transfer, 1+pub.length + this.signature.length + this.iv.length, this.encPrivKey.length);
		System.arraycopy(this.ciphertext, 0, transfer, 1+pub.length + this.signature.length + this.iv.length + this.encPrivKey.length, this.ciphertext.length);

		return transfer;
	}
	
	public static CSVFolderImpl createFromByteArray(byte[] input, Capability cap) {
		//FIXME: Make more generic
		byte[] pubkey = new byte[132];
		System.arraycopy(input, 1, pubkey, 0, pubkey.length);
		
		byte[] signature = new byte[128];
		System.arraycopy(input, 1+pubkey.length, signature, 0, signature.length);
		
		byte[] iv = new byte[16];
		System.arraycopy(input, 1+pubkey.length+signature.length, iv, 0, iv.length);
		
		byte[] encPrivKey = new byte[272];
		System.arraycopy(input, 1+pubkey.length+signature.length+iv.length, encPrivKey, 0, encPrivKey.length);
		
		byte[] cipherText = new byte[input.length - 1 - pubkey.length - signature.length - iv.length - encPrivKey.length];
		System.arraycopy(input, 1+pubkey.length+signature.length+iv.length+encPrivKey.length, cipherText, 0, cipherText.length);
		

		CSVFolderImpl foo = new CSVFolderImpl(cap, cipherText, pubkey, iv, signature);
		byte tmp[] = Cryptoutil.symECBDecrypt(encPrivKey, new SecretKeySpec(foo.getCapability().getKey(), Cryptoutil.SYM_CIPHER));
		foo.privkey = Cryptoutil.createRSAPrivateKey(tmp);
		return foo;
		
	}

}
