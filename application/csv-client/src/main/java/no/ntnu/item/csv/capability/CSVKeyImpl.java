package no.ntnu.item.csv.capability;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CSVKeyImpl implements CSVKey {

	private KeyType keyType;
	private byte[] key;

	public CSVKeyImpl() {

	}

	public CSVKeyImpl(KeyType keyType, byte[] key) {
		this.key = key;
		this.keyType = keyType;
	}

	@Override
	public KeyType getKeytype() {
		return this.keyType;
	}

	@Override
	public byte[] getKey() {
		return this.key;
	}

	public byte[] getNHash(int n, int truncate) {
		byte result[] = this.key;
		MessageDigest md;
		try {
			for (int i=0; i<n; i++) {
				md = MessageDigest.getInstance("SHA-256");
				md.update(result);
				result = md.digest();
			}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		byte[] truncated = new byte[truncate];
		System.arraycopy(result, 0, truncated, 0, truncate);
		return truncated;
	}

}
