package no.ntnu.item.csv.capability;

public enum CapabilityType {

	READ_ONLY, READ_WRITE, VERIFY;
	
	public String toString() {
		return this.name();
	}
	
}
