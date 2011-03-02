package no.ntnu.item.csv.capability;

public enum KeyType {
	
	WRITE_KEY, READ_KEY, VERIFY_KEY;
	
	public String toString() {
		return this.name();
	}
	
}
