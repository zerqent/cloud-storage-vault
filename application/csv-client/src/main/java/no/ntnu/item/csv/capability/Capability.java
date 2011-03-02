package no.ntnu.item.csv.capability;

public interface Capability {

	public byte[] getStorageIndex();
	
	public CSVKey getKey();
	
	public CapabilityType getType();
	
}
