package no.ntnu.item.csv.capability;

public interface Capability {

	public String getStorageIndex();
	
	public byte[] getKey();
	
	public byte[] getVerificationKey();
	
	public CapabilityType getType();
	
	public String toString();
	
}
