package no.ntnu.item.csv.csvobject.impl;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.csvobject.CSVFolder;

public class CSVFolderImpl implements CSVFolder{
	
	private byte[] pubkey;
	private byte[] privkey;
		
	private Map<CapabilityType, Capability> capabilities;
	private byte[] ciphertext;
	//private Map<String, Capability> contents; 
	
	public CSVFolderImpl() {
		this.capabilities = new HashMap<CapabilityType, Capability>();
		generateKeys();
	}
	
	public CSVFolderImpl(Capability capability, byte[] cipherText) {
		this.capabilities = new HashMap<CapabilityType, Capability>();
		this.capabilities.put(capability.getType(), capability);
		this.ciphertext = cipherText;
	}
	
	private void generateKeys() {
		
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		this.pubkey = pair.getPublic().getEncoded();
		this.privkey = pair.getPrivate().getEncoded();
		
		byte[] write = Cryptoutil.hash(this.privkey, 16);
		byte[] read = Cryptoutil.hash(write, 16);
		byte[] verify = Cryptoutil.hash(this.pubkey, 16);
		
		Capability writecap = new CapabilityImpl(CapabilityType.RW, write, verify);
		Capability readcap = new CapabilityImpl(CapabilityType.RO, read, verify);
		Capability verifycap = new CapabilityImpl(CapabilityType.V, null, verify);
		
		this.capabilities.put(writecap.getType(), writecap);
		this.capabilities.put(readcap.getType(), readcap);
		this.capabilities.put(verifycap.getType(), verifycap);
		
	}
	
	private void createPlainText() {
		
		
	}
	
	@Override
	public void encrypt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decrypt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verify() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPlainText(byte[] plainText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCipherText(byte[] cipherText) {
		// TODO Auto-generated method stub
		
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
		if (this.capabilities.containsKey(CapabilityType.RW)) {
			return this.capabilities.get(CapabilityType.RW);
		} else if(this.capabilities.containsKey(CapabilityType.RO)) {
			return this.capabilities.get(CapabilityType.RO);
		} else if(this.capabilities.containsKey(CapabilityType.V)) {
			return this.capabilities.get(CapabilityType.V);
		}
		return null;
	}

	@Override
	public void setCapability(Capability capability) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<CapabilityType, Capability> getCapabilitites() {
		return this.capabilities;
	}
}
