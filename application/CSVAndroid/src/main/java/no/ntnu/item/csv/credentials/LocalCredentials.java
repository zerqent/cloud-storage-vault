package no.ntnu.item.csv.credentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.cryptoutil.KeyChain;
import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.man.CSVFileManager;
import no.ntnu.item.csv.exception.IncorrectPasswordException;
import android.app.Activity;
import android.content.Context;

public class LocalCredentials {
	public static final String SEPARATOR = "¦";
	public static final String save_file = "my_credentials.csv";
	private Capability rootCapability;
	private Context ctx;
	private String onLineUserName;
	private String onLinePassword;

	// public LocalCredentials(Context ctx, Capability cap, String password) {
	// this.ctx = ctx;
	// this.rootCapability = cap;
	// writeCredentialsToDisk(password);
	// }

	private LocalCredentials() {

	}

	// public LocalCredentials(Context ctx, String password, boolean createNew)
	// throws IncorrectPasswordException {
	// this.ctx = ctx;
	// if (createNew) {
	// CSVFolder rootFolder = new CSVFolder();
	// CSVFolder shareFolder = new CSVFolder();
	//
	// rootFolder.addContent(CSVFileManager.SHARE_FOLDER,
	// shareFolder.getCapability());
	// rootFolder.encrypt();
	// shareFolder.encrypt();
	//
	// this.rootCapability = rootFolder.getCapability();
	// Communication.put(rootFolder, Communication.SERVER_PUT);
	// Communication.put(shareFolder, Communication.SERVER_PUT);
	//
	// writeCredentialsToDisk(password);
	// } else {
	// try {
	// FileInputStream in = ctx
	// .openFileInput(LocalCredentials.save_file);
	// // We know the size of the salt and encrypted capability
	// byte[] salt = new byte[8];
	// byte[] encCap = new byte[64];
	//
	// int b;
	// for (int i = 0; (b = in.read()) != -1; i++) {
	// if (i < salt.length)
	// salt[i] = (byte) b;
	// else
	// encCap[i - salt.length] = (byte) b;
	// }
	//
	// KeyChain kc = new KeyChain(password, salt);
	// byte[] cap = Cryptoutil.symECBDecrypt(encCap, kc.getKey());
	// if (cap != null) {
	// String stringCap = new String(cap);
	// this.rootCapability = CapabilityImpl.fromString(stringCap);
	// } else {
	// throw new IncorrectPasswordException();
	// }
	// } catch (FileNotFoundException e) {
	// // This is our first time running the application
	// return;
	// } catch (IOException e) {
	// return;
	// }
	// }
	//
	// }

	// public boolean isFirstStart() {
	// return this.rootCapability == null;
	// }

	public Capability getRootCapability() {
		return this.rootCapability;
	}

	public static LocalCredentials openExistingLocalCredentials(
			Activity activity, String password)
			throws IncorrectPasswordException {
		LocalCredentials localCredentials = new LocalCredentials();

		try {
			FileInputStream in = activity
					.openFileInput(LocalCredentials.save_file);
			File tmp = new File(activity.getFilesDir(),
					LocalCredentials.save_file);
			byte[] b = new byte[(int) tmp.length()];
			in.read(b);
			in.close();
			byte[] salt = new byte[8];
			byte[] cipherText = new byte[b.length - salt.length];
			System.arraycopy(b, 0, salt, 0, salt.length);
			System.arraycopy(b, salt.length, cipherText, 0, cipherText.length);

			KeyChain keyChain = new KeyChain(password, salt);
			byte[] plainText = Cryptoutil.symECBDecrypt(cipherText,
					keyChain.getKey());
			String[] text = new String(plainText).split(SEPARATOR);
			localCredentials.onLineUserName = text[1];
			localCredentials.onLinePassword = text[2];
			localCredentials.rootCapability = CapabilityImpl
					.fromString(text[0]);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CSVActivity.connection.setUsername(localCredentials.onLineUserName);
		CSVActivity.connection.setPassword(localCredentials.onLinePassword);
		return localCredentials;
	}

	public static LocalCredentials createNewLocalCredentials(Activity activity,
			String password) {

		CSVFolder rootFolder = new CSVFolder();
		CSVFolder shareFolder = new CSVFolder();

		rootFolder.addContent(CSVFileManager.SHARE_FOLDER,
				shareFolder.getCapability());
		rootFolder.encrypt();
		shareFolder.encrypt();

		// CSVActivity.connection.setUsername(onLineUserName);
		// CSVActivity.connection.setPassword(onLinePassword);

		CSVActivity.connection.put(rootFolder);
		CSVActivity.connection.put(shareFolder);

		// Communication.put(rootFolder, Communication.SERVER_PUT);
		// Communication.put(shareFolder, Communication.SERVER_PUT);

		return importExistingLocalCredentials(activity, password,
				rootFolder.getCapability());

	}

	public static LocalCredentials importExistingLocalCredentials(
			Activity activity, String password, Capability rootCapability) {
		LocalCredentials localCredentials = new LocalCredentials();
		localCredentials.onLinePassword = CSVActivity.connection.getPassword();
		localCredentials.onLineUserName = CSVActivity.connection.getUsername();
		localCredentials.rootCapability = rootCapability;

		KeyChain keyChain = new KeyChain(password);

		String plainText = rootCapability.toString() + SEPARATOR
				+ localCredentials.onLineUserName + SEPARATOR
				+ localCredentials.onLinePassword;

		byte[] cipherText = Cryptoutil.symECBEncrypt(plainText.getBytes(),
				keyChain.getKey());
		byte[] salt = keyChain.getSalt();
		byte[] writeOut = new byte[cipherText.length + salt.length];
		System.arraycopy(salt, 0, writeOut, 0, salt.length);
		System.arraycopy(cipherText, 0, writeOut, salt.length,
				cipherText.length);

		FileOutputStream out;

		try {
			out = activity.openFileOutput(save_file, Context.MODE_PRIVATE);
			out.write(writeOut);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// eiriha komle123

		// CSVActivity.connection.setUsername(onLineUserName);
		// CSVActivity.connection.setPassword(onLinePassword);
		return localCredentials;
	}
}
