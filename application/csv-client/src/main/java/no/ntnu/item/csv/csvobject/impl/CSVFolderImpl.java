package no.ntnu.item.csv.csvobject.impl;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.csvobject.CSVFolder;

public class CSVFolderImpl implements CSVFolder{
	
	private byte[] pubkey;
	private byte[] privkey;
		
	//private Map<CapabilityType, Capability> capabilities;
	private Capability capability;
	private Map<String, Capability> contents; 
	
	private byte[] ciphertext;
	private byte[] plainText;
	private byte[] iv;
	
	private byte[] signature;
	
	public CSVFolderImpl() {
		contents = new HashMap<String, Capability>();
		generateKeys();
	}
	
	public CSVFolderImpl(Capability capability, byte[] cipherText, byte[] iv, byte[] pubkey, byte[] signature) {
		this.capability = capability;
		this.ciphertext = cipherText;
		this.iv = iv;
		this.pubkey = pubkey;
		this.signature = signature;
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
	
	private void createPlainText() {
		String plaintext = "";
		for (Iterator<String> iterator = this.contents.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Capability cap = this.contents.get(key);
			plaintext += key + ";" + cap.toString() + "\n";
		}
		this.plainText = plaintext.getBytes(); 
	}
	
	private void createContentsFromPlainText() {
		this.contents = new HashMap<String, Capability>();
		String contents = new String(this.plainText);
		String[] lines = contents.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
			String[] lineCont = lines[i].split(";");
			Capability cap = CapabilityImpl.fromString(lineCont[1]);
			String alias = lineCont[0];
			this.contents.put(alias,cap);
		}
		
	}
	
	private void sign() {
		byte[] hash = Cryptoutil.hash(this.ciphertext, -1);
		try {
			PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(this.privkey));
			this.signature = Cryptoutil.signature(hash, privateKey);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void verify() {
		
	}

	@Override
	public void setPlainText(byte[] plainText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCipherText(byte[] cipherText) {
		this.ciphertext = cipherText;
		
	}

	@Override
	public byte[] getPlainText() {
		// TODO Auto-generated method stub
		return null;
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
}
