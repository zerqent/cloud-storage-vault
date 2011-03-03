package no.ntnu.item.csv.capability;

import no.ntnu.item.cryptoutil.Cryptoutil;

import org.apache.commons.codec.binary.Base64;

public class CapabilityImpl implements Capability {
	
	private byte[] storageIndex;
	private byte[] key;
	private byte[] verification;
	private CapabilityType type;
	
	public CapabilityImpl() {
	}
	
	public CapabilityImpl(CapabilityType type, byte[] storageIndex, byte[] key, byte[] verificationKey) {
		this.storageIndex = storageIndex;
		this.type = type;
		this.key = key;
		this.verification = verificationKey;
		
	}
	
	public CapabilityImpl(CapabilityType type, byte[] key, byte[] verification) {
		this.type = type;
		this.key = key;
		this.verification = verification;
		
		switch(this.type) {
		case READ_WRITE:
			this.storageIndex = Cryptoutil.nHash(this.key, 2, 16);
			break;
		case READ_ONLY:
			this.storageIndex = Cryptoutil.nHash(this.key, 1, 16);
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
	public byte[] getKey() {
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

	@Override
	public byte[] getVerificationKey() {
		return this.verification;
	}

}
