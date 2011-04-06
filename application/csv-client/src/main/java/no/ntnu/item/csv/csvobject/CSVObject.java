package no.ntnu.item.csv.csvobject;

import no.ntnu.item.csv.capability.Capability;

public interface CSVObject {

	public boolean isValid();

	public Capability getCapability();

	public void setCapability(Capability capability);

	// public void upload(OutputStream cipherOutput);

	// public OutputStream download();

}
