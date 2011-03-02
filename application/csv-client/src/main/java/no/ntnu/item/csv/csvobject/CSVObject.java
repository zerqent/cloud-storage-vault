package no.ntnu.item.csv.csvobject;

import no.ntnu.item.csv.capability.Capability;

public interface CSVObject {
	
	public void encrypt();
	
	public void decrypt();
	
	public void verify();
	
	public void setPlainText(byte[] plainText); 
	
	public void setCipherText(byte[] cipherText);
	
	public byte[] getPlainText();
	
	public byte[] getCipherText();
	
	public Capability getCapability();
	
	public void setCapability(Capability capability);
}
