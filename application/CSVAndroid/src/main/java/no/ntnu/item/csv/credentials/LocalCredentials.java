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
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.IncorrectPasswordException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.filemanager.CSVFileManager;
import android.app.Activity;
import android.content.Context;

public class LocalCredentials {
	public static final String SEPARATOR = "¦";
	public static final String save_file = "my_credentials.csv";
	private Capability rootCapability;
	private String onLineUserName;
	private String onLinePassword;
	private String hostname;
	private String scheme;
	private int port;

	private LocalCredentials() {

	}

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
			byte[] salt = new byte[KeyChain.SALT_SIZE / 8];
			byte[] cipherText = new byte[b.length - salt.length];
			System.arraycopy(b, 0, salt, 0, salt.length);
			System.arraycopy(b, salt.length, cipherText, 0, cipherText.length);

			KeyChain keyChain = new KeyChain(password, salt);
			byte[] plainText = Cryptoutil.symECBDecrypt(cipherText,
					keyChain.getKey());

			if (plainText == null || plainText.length < 10) {
				throw new IncorrectPasswordException();
			}

			String[] text = new String(plainText).split(SEPARATOR);

			if (text.length < 6) {
				throw new IncorrectPasswordException();
			}

			localCredentials.onLineUserName = text[1];
			localCredentials.onLinePassword = text[2];
			localCredentials.scheme = text[3];
			localCredentials.hostname = text[4];
			localCredentials.port = Integer.parseInt(text[5]);

			localCredentials.rootCapability = CapabilityImpl
					.fromString(text[0]);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CSVActivity.connection.setPort(localCredentials.port);
		CSVActivity.connection.setScheme(localCredentials.scheme);
		CSVActivity.connection.setHostname(localCredentials.hostname);
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
		// rootFolder.encrypt();
		// shareFolder.encrypt();

		// CSVActivity.connection.put(rootFolder);
		try {
			rootFolder = CSVActivity.fm.uploadFolder(rootFolder);
			shareFolder = CSVActivity.fm.uploadFolder(shareFolder);
		} catch (ServerCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidWriteEnablerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImmutableFileExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// CSVActivity.connection.put(shareFolder);

		return importExistingLocalCredentials(activity, password,
				rootFolder.getCapability());
	}

	public static LocalCredentials importExistingLocalCredentials(
			Activity activity, String password, Capability rootCapability) {
		LocalCredentials localCredentials = new LocalCredentials();
		localCredentials.scheme = CSVActivity.connection.getScheme();
		localCredentials.hostname = CSVActivity.connection.getHostname();
		localCredentials.port = CSVActivity.connection.getPort();
		localCredentials.onLinePassword = CSVActivity.connection.getPassword();
		localCredentials.onLineUserName = CSVActivity.connection.getUsername();
		localCredentials.rootCapability = rootCapability;

		KeyChain keyChain = new KeyChain(password);

		String plainText = rootCapability.toString() + SEPARATOR
				+ localCredentials.onLineUserName + SEPARATOR
				+ localCredentials.onLinePassword + SEPARATOR
				+ localCredentials.scheme + SEPARATOR
				+ localCredentials.hostname + SEPARATOR
				+ String.valueOf(localCredentials.port);

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
		return localCredentials;
	}
}
