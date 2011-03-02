package no.ntnu.item.csv.capability;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class CapabilityImpl implements Capability {
	
	private byte[] storageIndex;
	private CSVKey key;
	private CapabilityType type;
	
	public CapabilityImpl() {
	}
	
	public CapabilityImpl(byte[] storageIndex, CSVKey key, CapabilityType type) {
		this.storageIndex = storageIndex;
		this.key = key;
		this.type = type;
	}
	
	public CapabilityImpl(CSVKey key, CapabilityType type) {
		this.type = type;
		this.key = key;
		
		MessageDigest md;
		byte[] tmp;
		
		try {
			md = MessageDigest.getInstance("SHA-256");
					
			switch(type) {
				case READ_WRITE:
					md.update(key.getKey());
					tmp = md.digest();
					md = MessageDigest.getInstance("SHA-256");
					md.update(tmp);
					this.storageIndex = md.digest();
					break;
				case READ_ONLY:
					md = MessageDigest.getInstance("SHA-256");
					md.update(key.getKey());
					this.storageIndex = md.digest();
					break;
				case VERIFY:
					break;
			}
						
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public String getStorageIndex() {
		return Base64.encodeBase64String(this.storageIndex);
	}

	@Override
	public CSVKey getKey() {
		return this.key;
	}

	@Override
	public CapabilityType getType() {
		return this.type;	
	}

}
