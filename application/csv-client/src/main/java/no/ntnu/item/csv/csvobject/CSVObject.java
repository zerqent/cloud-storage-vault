package no.ntnu.item.csv.csvobject;

import no.ntnu.item.csv.capability.Capability;

public interface CSVObject {

	public void encrypt();

	public void decrypt();

	public boolean isValid();

	public byte[] getCipherText();

	public Capability getCapability();

	public void setCapability(Capability capability);
	
	public byte[] getTransferArray();
	
}
