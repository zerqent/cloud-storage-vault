package no.ntnu.item.csv.capability;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;

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
		case RW:
			this.storageIndex = Cryptoutil.nHash(this.key, 2, 16);
			break;
		case RO:
			this.storageIndex = Cryptoutil.nHash(this.key, 1, 16);
			break;
		case V:
			break;
		}

	}

	@Override
	public String getStorageIndex() {
		//return Base64.encodeBase64String(this.storageIndex);
		return Base32.encode(this.storageIndex);
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

	public String toString() {
		String toString = "";
		//toString += this.type.name() + ":" + Base64.encodeBase64String(this.key);
		toString += this.type.name() + ":" + Base32.encode(this.key);
		if (this.verification != null) {
			//toString += ":" + Base64.encodeBase64String(this.verification);
			toString += ":" + Base32.encode(this.verification);
		}
		return toString;
	}

	public static Capability fromString(String capability) {
		String[] content = capability.split(":");
		CapabilityType type = CapabilityType.valueOf(content[0]);
		//byte[] key = Base64.decodeBase64(content[1]);
		byte[] key = Base32.decode(content[1]);
		byte[] verify = null;
		if (content.length>2) {
			//verify = Base64.decodeBase64(content[2]);
			verify = Base32.decode(content[2]);
		}

		return new CapabilityImpl(type, key, verify);
	}

	@Override
	public byte[] getWriteEnabler() {
		// TODO: Figure out if the message is somehow important for security.
		byte[] taggedmsg = {'t','h','i','s','i','s','h','m','a','c','f','o','r'}; 
		if (this.type == CapabilityType.RW) {
			byte[] tmp = Cryptoutil.hmac(taggedmsg, this.key);
			byte[] tmp2 = new byte[16];
			System.arraycopy(tmp, 0, tmp2, 0, tmp2.length);
			return tmp2;
		} else {
			return null;
		}
	}

}
