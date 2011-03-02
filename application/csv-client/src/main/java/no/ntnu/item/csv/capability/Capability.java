package no.ntnu.item.csv.capability;

public interface Capability {

	public String getStorageIndex();
	
	public CSVKey getKey();
	
	public CapabilityType getType();
	
}
