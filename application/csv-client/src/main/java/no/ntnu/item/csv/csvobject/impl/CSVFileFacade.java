package no.ntnu.item.csv.csvobject.impl;

import java.io.File;
import java.io.IOException;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFile;

public class CSVFileFacade implements CSVFile {
	
	private CSVFileImplHelper helper;
	
	public CSVFileFacade() {
		this.helper = new CSVFileImplHelper();
	}
	
	@Override
	public void encrypt() {
		this.helper.encrypt();
	}

	@Override
	public void decrypt() {
		this.helper.decrypt();
	}

	@Override
	public void verify() {
		this.helper.verify();
	}

	@Override
	public void setPlainText(byte[] plainText) {
		this.helper.setPlainText(plainText);
	}

	@Override
	public void setCipherText(byte[] cipherText) {
		this.helper.setCipherText(cipherText);
	}

	@Override
	public byte[] getPlainText() {
		return this.helper.getPlainText();
	}

	@Override
	public byte[] getCipherText() {
		return this.helper.getCipherText();
	}

	@Override
	public void setPlainText(File f) throws IOException {
		this.helper.setPlainText(f);
	}

	@Override
	public Capability getCapability() {
		return this.helper.getCapability();
	}

	@Override
	public void setCapability(Capability capability) {
		this.helper.setCapability(capability);
	}

}
