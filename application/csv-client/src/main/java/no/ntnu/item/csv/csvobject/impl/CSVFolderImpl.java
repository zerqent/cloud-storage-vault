package no.ntnu.item.csv.csvobject.impl;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.CSVKey;
import no.ntnu.item.csv.capability.CSVKeyImpl;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.capability.KeyType;

public class CSVFolderImpl {
	
	private byte[] pubkey;
	private byte[] privkey;
		
	private Map<CapabilityType, Capability> capabilities;
//	private byte[] ciphertext;
//	private Map<String, Capability> contents; 
	
	public CSVFolderImpl() {
		this.capabilities = new HashMap<CapabilityType, Capability>();
		generateKeys();
	}
	
	private void generateKeys() {
		
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		this.pubkey = pair.getPublic().getEncoded();
		this.privkey = pair.getPrivate().getEncoded();
		
		CSVKey write_k = new CSVKeyImpl(KeyType.WRITE_KEY, Cryptoutil.hash(this.privkey, 16));
		CSVKey read_k = new CSVKeyImpl(KeyType.READ_KEY, Cryptoutil.nHash(this.privkey, 2, 16));
		CSVKey verify_k = new CSVKeyImpl(KeyType.VERIFY_KEY, Cryptoutil.hash(this.pubkey, 16));
		
		Capability write = new CapabilityImpl(write_k, CapabilityType.READ_WRITE);
		Capability read = new CapabilityImpl(read_k, CapabilityType.READ_ONLY);
		Capability verify = new CapabilityImpl(verify_k, CapabilityType.VERIFY);
		
		this.capabilities.put(write.getType(), write);
		this.capabilities.put(read.getType(), read);
		this.capabilities.put(verify.getType(), verify);
		
	}
	
}
