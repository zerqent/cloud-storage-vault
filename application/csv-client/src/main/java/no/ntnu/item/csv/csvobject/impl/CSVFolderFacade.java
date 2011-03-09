package no.ntnu.item.csv.csvobject.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.csvobject.CSVFolder;

public abstract class CSVFolderFacade implements CSVFolder {

	public CSVFolderFacade() {

	}

	public CSVFolderFacade(Capability cap, byte[] ciperText, byte[] publicKey, byte[] IV, byte[] signature) {
		this.setCipherText(ciperText);
		this.setCapability(cap);
		this.setPubKey(publicKey);
		this.setIV(IV);
		this.setSignature(signature);
	}

	protected abstract void setPlainText(byte[] plainText);

	protected abstract void setCipherText(byte[] cipherText);

	protected abstract byte[] getPlainText();

	protected abstract void setPubKey(byte[] pubKey);

	protected abstract void setIV(byte[] IV);

	protected abstract void setContents(Map<String, Capability> contents);

	protected abstract void setSignature(byte [] signature);

	protected void createPlainText() {
		String plaintext = "";
		for (Iterator<String> iterator = this.getContents().keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			Capability cap = this.getContents().get(key);
			plaintext += key + ";" + cap.toString() + "\n";
		}
		this.setPlainText(plaintext.getBytes()); 
	}

	protected void createContentsFromPlainText() {
		Map<String, Capability> contents = new HashMap<String, Capability>();
		String strCont = new String(this.getPlainText());
		String[] lines = strCont.split("\n");

		if (lines.length==1 && lines[0].isEmpty()) {
			this.setContents(contents);
			return;
		}

		for (int i = 0; i < lines.length; i++) {
			String[] lineCont = lines[i].split(";");
			Capability cap = CapabilityImpl.fromString(lineCont[1]);
			String alias = lineCont[0];
			contents.put(alias,cap);
		}
		this.setContents(contents);
	}
}
