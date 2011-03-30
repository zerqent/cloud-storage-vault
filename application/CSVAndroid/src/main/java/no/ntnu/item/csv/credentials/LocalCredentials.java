package no.ntnu.item.csv.credentials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.cryptoutil.KeyChain;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.man.CSVFileManager;
import no.ntnu.item.csv.exception.IncorrectPasswordException;
import android.content.Context;

public class LocalCredentials {

	public static final String save_file = "my_credentials.csv";
	private Capability rootCapability;
	private Context ctx;

	public LocalCredentials(Context ctx, Capability cap, String password) {
		this.ctx = ctx;
		this.rootCapability = cap;
		writeCredentialsToDisk(password);
	}

	public LocalCredentials(Context ctx, String password, boolean createNew)
			throws IncorrectPasswordException {
		this.ctx = ctx;
		if (createNew) {
			CSVFolder rootFolder = new CSVFolder();
			CSVFolder shareFolder = new CSVFolder();

			rootFolder.addContent(CSVFileManager.SHARE_FOLDER,
					shareFolder.getCapability());
			rootFolder.encrypt();
			shareFolder.encrypt();

			this.rootCapability = rootFolder.getCapability();
			Communication.put(rootFolder, Communication.SERVER_PUT);
			Communication.put(shareFolder, Communication.SERVER_PUT);

			writeCredentialsToDisk(password);
		} else {
			try {
				FileInputStream in = ctx
						.openFileInput(LocalCredentials.save_file);
				// We know the size of the salt and encrypted capability
				byte[] salt = new byte[8];
				byte[] encCap = new byte[64];

				int b;
				for (int i = 0; (b = in.read()) != -1; i++) {
					if (i < salt.length)
						salt[i] = (byte) b;
					else
						encCap[i - salt.length] = (byte) b;
				}

				KeyChain kc = new KeyChain(password, salt);
				byte[] cap = Cryptoutil.symECBDecrypt(encCap, kc.getKey());
				if (cap != null) {
					String stringCap = new String(cap);
					this.rootCapability = CapabilityImpl.fromString(stringCap);
				} else {
					throw new IncorrectPasswordException();
				}
			} catch (FileNotFoundException e) {
				// This is our first time running the application
				return;
			} catch (IOException e) {
				return;
			}
		}

	}

	private void writeCredentialsToDisk(String password) {
		try {
			System.out.println("Writing to file");
			System.out.println(this.rootCapability.toString());
			KeyChain kc = new KeyChain(password);

			byte[] encRootCap = Cryptoutil.symECBEncrypt(this.rootCapability
					.toString().getBytes(), kc.getKey());
			byte[] content = new byte[encRootCap.length + kc.getSalt().length];

			System.arraycopy(kc.getSalt(), 0, content, 0, kc.getSalt().length);
			System.arraycopy(encRootCap, 0, content, kc.getSalt().length,
					encRootCap.length);

			FileOutputStream out = ctx.openFileOutput(
					LocalCredentials.save_file, Context.MODE_PRIVATE);
			out.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isFirstStart() {
		return this.rootCapability == null;
	}

	public Capability getRootCapability() {
		return this.rootCapability;
	}

}
