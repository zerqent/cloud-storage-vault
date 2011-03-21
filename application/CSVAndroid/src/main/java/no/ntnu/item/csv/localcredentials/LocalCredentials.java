package no.ntnu.item.csv.localcredentials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFolder;
import android.content.Context;

public class LocalCredentials {

	public static final String save_file = "my_credentials.csv";
	private Capability rootCapability;

	public LocalCredentials(Context ctx, boolean createNew) {
		if (createNew) {
			try {
				CSVFolder rootFolder = new CSVFolder();
				rootFolder.encrypt();
				this.rootCapability = rootFolder.getCapability();
				Communication.put(rootFolder, Communication.SERVER_PUT);

				FileOutputStream out = ctx.openFileOutput(
						LocalCredentials.save_file, Context.MODE_PRIVATE);
				out.write(this.rootCapability.toString().getBytes());
				out.close();

			} catch (FileNotFoundException e) {
				// If this exception triggers I will be impressed...
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				FileInputStream in = ctx
						.openFileInput(LocalCredentials.save_file);
				// We know the size of the capability
				byte[] cap = new byte[58];
				in.read(cap);
				String stringCap = new String(cap);
				this.rootCapability = CapabilityImpl.fromString(stringCap);

			} catch (FileNotFoundException e) {
				// This is our first time running the application
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public boolean isFirstStart() {
		return this.rootCapability == null;
	}

	public Capability getRootCapability() {
		return this.rootCapability;
	}

}
