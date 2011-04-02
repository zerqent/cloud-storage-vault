package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.firststart.FirstStartActivity;
import android.app.Activity;
import android.os.AsyncTask;

public class CreateRootCapTask extends AsyncTask<Void, Void, Capability> {

	private Activity caller;
	private String password;

	public CreateRootCapTask(Activity caller, String password) {
		this.caller = caller;
		this.password = password;
	}

	@Override
	protected Capability doInBackground(Void... params) {
		LocalCredentials creds;
		creds = LocalCredentials.createNewLocalCredentials(caller, password);
		return creds.getRootCapability();
	}

	@Override
	protected void onPostExecute(Capability result) {
		System.out.println("postExecute");
		if (caller instanceof FirstStartActivity) {
			FirstStartActivity first = (FirstStartActivity) caller;
			first.supplyWithRootCap(result);
		}
	}

}
