package no.ntnu.item.csv.capability;

import no.ntnu.item.cryptoutil.Cryptoutil;

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
		
		switch(this.type) {
		case READ_WRITE:
			this.storageIndex = Cryptoutil.nHash(this.key.getKey(), 2, 16);
			break;
		case READ_ONLY:
			this.storageIndex = Cryptoutil.nHash(this.key.getKey(), 1, 16);
			break;
		case VERIFY:
			break;
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
	
	public void setStorageIndex(byte[] storageIndex) {
		this.storageIndex = storageIndex;
	}
	
	public byte[] getStorageIndexByte() {
		return this.storageIndex;
	}

}
