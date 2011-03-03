package no.ntnu.item.csv.capability;

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
}
