package no.ntnu.item.csv.csvobject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import no.ntnu.item.cryptoutil.CryptoStreamer;
import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

public class CSVFile implements CSVObject {

	private Capability capability;
	private File file;
	private CryptoStreamer cryptoStreamer;
	private byte[] recordedDigest;

	public CSVFile(File f) throws FileNotFoundException {
		if (f == null) {
			throw new IllegalArgumentException("File can not be null");
		}

		this.cryptoStreamer = new CryptoStreamer();
		this.file = f;
		this.capability = new CapabilityImpl(CapabilityType.RO,
				this.cryptoStreamer.getSecretKey().getEncoded(), null, true);
		if (!f.exists() || !f.canRead()) {
			throw new FileNotFoundException();
		}
	}

	public CSVFile(Capability capability, File f) {
		this.setCapability(capability);
		this.file = f;
	}

	@Override
	public Capability getCapability() {
		return this.capability;
	}

	@Override
	public void setCapability(Capability capability) {
		this.capability = capability;
		this.cryptoStreamer = new CryptoStreamer(this.capability.getKey(),
				Cryptoutil.nHash(this.capability.getKey(), 2,
						Cryptoutil.SYM_BLOCK_SIZE / 8));
	}

	@Override
	public boolean isValid() {
		if (this.recordedDigest == null) {
			// Means we haven't actually read/uploaded the object yet.
			return false;
		}
		byte[] calculatedDigest = this.recordedDigest;
		int n = calculatedDigest.length;
		byte[] storedKey = this.capability.getVerificationKey();

		for (int i = 0; i < n; i++) {
			if (calculatedDigest[i] != storedKey[i]) {
				return false;
			}
		}
		return true;
	}

	public OutputStream download() {
		try {
			return this.cryptoStreamer
					.getDecryptedAndHashedOutputStream(new FileOutputStream(
							this.file.getAbsolutePath()));
		} catch (FileNotFoundException e) {
			// Should not happen, as long as we have write access to sdcard and
			// the filename we are trying to write
			return null;
		}
	}

	public InputStream upload() {
		try {
			return this.cryptoStreamer
					.getEncryptedAndHashedInputStream(new FileInputStream(
							this.file));
		} catch (FileNotFoundException e) {
			// Constructor should check this
			return null;
		}
	}

	public long getContentLength() {
		// We must predict the length of the ciphertext (blocks of 64 byte)
		long div = this.file.length() / 16;
		long mod = this.file.length() % 16;
		if (mod != 0) {
			div = div + 1;
		}
		return div * 16;
	}

	public void finishedUpload() {
		this.recordedDigest = this.cryptoStreamer.finish();
		this.capability.setVerification(this.recordedDigest);
	}

	public void finishedDownload() {
		// this.cryptoStreamer.closeStreams();
		this.recordedDigest = this.cryptoStreamer.finish();
	}

	public File getFile() {
		return this.file;
	}

}
