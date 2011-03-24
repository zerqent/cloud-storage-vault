package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.firststart.FirstStartActivity;
import android.app.Activity;
import android.os.AsyncTask;

public class CreateRootCapTask extends AsyncTask<Void, Void, Capability> {

	private Activity caller;

	public CreateRootCapTask(Activity caller) {
		this.caller = caller;
	}

	@Override
	protected Capability doInBackground(Void... params) {
		LocalCredentials creds = new LocalCredentials(caller, true);
		return creds.getRootCapability();

	}

	@Override
	protected void onPostExecute(Capability result) {
		if (caller instanceof FirstStartActivity) {
			FirstStartActivity first = (FirstStartActivity) caller;
			first.supplyWithRootCap(result);
		}
	}

}
