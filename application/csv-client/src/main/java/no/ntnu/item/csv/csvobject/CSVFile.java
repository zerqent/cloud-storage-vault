package no.ntnu.item.csv.csvobject;

import java.io.File;
import java.io.IOException;

public interface CSVFile extends CSVObject {

	public void setPlainText(File f) throws IOException;

	public byte[] getPlainText();

}
