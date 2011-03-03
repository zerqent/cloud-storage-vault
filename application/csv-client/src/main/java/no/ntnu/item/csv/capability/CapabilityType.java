package no.ntnu.item.csv.capability;

public enum CapabilityType {

	RO("READ_ONLY"), RW("READ_WRITE"), V("VERIFY");
	
	private String humanreadable;
	
	private CapabilityType(String humanreadable) {
		this.humanreadable = humanreadable;
	}
	
	public String getHumanReadable() {
		return this.humanreadable;
	}
	
}
