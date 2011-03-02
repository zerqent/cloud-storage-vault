package no.ntnu.item.csv.capability;

public enum CapabilityType {

	READ_ONLY("RO:"), READ_WRITE("RW:"), VERIFY("V:");
	
	private String prefix;
	
	private CapabilityType(String prefix) {
		this.prefix = prefix;
	}
	
	public String toString() {
		return this.name();
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
}
