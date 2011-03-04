package no.ntnu.item.csv.csvobject;

import java.util.Map;

import no.ntnu.item.csv.capability.Capability;

public interface CSVFolder extends CSVObject {

	public Map<String, Capability> getContents();
	
}
