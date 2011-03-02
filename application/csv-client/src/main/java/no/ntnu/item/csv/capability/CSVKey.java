package no.ntnu.item.csv.capability;

public interface CSVKey {
	
	public KeyType getKeytype();
	
	public byte[] getKey();
	
	public byte[] getNHash(int n, int truncate);
	
	
}
