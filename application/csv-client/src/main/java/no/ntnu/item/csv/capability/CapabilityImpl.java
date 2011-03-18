package no.ntnu.item.csv.capability;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;

public class CapabilityImpl implements Capability {

	private byte[] storageIndex;
	private byte[] key;
	private byte[] verification;
	private CapabilityType type;
	private boolean isFile;

	public CapabilityImpl() {
	}

	public CapabilityImpl(CapabilityType type, byte[] storageIndex, byte[] key,
			byte[] verificationKey, boolean isFile) {
		this.storageIndex = storageIndex;
		this.type = type;
		this.key = key;
		this.verification = verificationKey;
		this.isFile = isFile;
	}

	public CapabilityImpl(CapabilityType type, byte[] key, byte[] verification,
			boolean isFile) {
		this.type = type;
		this.key = key;
		this.verification = verification;
		this.isFile = isFile;

		switch (this.type) {
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
		// return Base64.encodeBase64String(this.storageIndex);
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

	@Override
	public String toString() {
		String toString = "";
		if (this.isFile) {
			toString += "F:";
		} else {
			toString += "D:";
		}

		toString += this.type.name() + ":" + Base32.encode(this.key);
		if (this.verification != null) {
			toString += ":" + Base32.encode(this.verification);
		}
		return toString;
	}

	public static Capability fromString(String capability) {
		String[] content = capability.split(":");
		boolean isFile = true;

		if (content[0].equals("D")) {
			isFile = false;
		}
		CapabilityType type = CapabilityType.valueOf(content[1]);

		byte[] key = Base32.decode(content[2]);
		byte[] verify = null;
		if (content.length > 3) {
			verify = Base32.decode(content[3]);
		}

		return new CapabilityImpl(type, key, verify, isFile);
	}

	@Override
	public byte[] getWriteEnabler() {
		// TODO: Figure out if the message is somehow important for security.
		byte[] taggedmsg = { 't', 'h', 'i', 's', 'i', 's', 'h', 'm', 'a', 'c',
				'f', 'o', 'r' };
		if (this.type == CapabilityType.RW) {
			byte[] tmp = Cryptoutil.hmac(taggedmsg, this.key);
			byte[] tmp2 = new byte[16];
			System.arraycopy(tmp, 0, tmp2, 0, tmp2.length);
			return tmp2;
		} else {
			return null;
		}
	}

	@Override
	public boolean isFolder() {
		return !this.isFile;
	}

	@Override
	public boolean isFile() {
		return this.isFile;
	}

}
