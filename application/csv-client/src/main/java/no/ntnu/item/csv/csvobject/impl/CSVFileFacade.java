package no.ntnu.item.csv.csvobject.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFile;

public abstract class CSVFileFacade implements CSVFile {

	public CSVFileFacade(File f) throws IOException {
		this.setPlainText(f);
	}

	public CSVFileFacade(Capability cap, byte[] cipherText) {
		this.setCapability(cap);
		this.setCipherText(cipherText);
	}

	public static byte[] readDataBinary(InputStream in, int filelength) throws IOException {
		//TODO: 32-bit warning right here..
		byte[] bytes = new byte[filelength];
		int offset = 0;
		int numRead = 0;

		while (offset < bytes.length && (numRead=in.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not read entire file");
		}
		return bytes;
	}

	@Override
	public abstract void encrypt();

	@Override
	public abstract void decrypt();

	@Override
	public abstract boolean isValid();

	@Override
	public abstract void setPlainText(File f) throws IOException;

	@Override
	public abstract Capability getCapability();

	@Override
	public abstract byte[] getCipherText();

	protected abstract void setCipherText(byte[] cipherText);

}