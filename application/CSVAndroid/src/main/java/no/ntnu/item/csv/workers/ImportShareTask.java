package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class ImportShareTask extends AsyncTask<String, Void, String> {

	private Capability capability;
	private Activity caller;

	public ImportShareTask(Activity caller, Capability capability) {
		this.capability = capability;
	}

	@Override
	protected String doInBackground(String... params) {
		if (params != null && params.length > 0) {
			String username = params[0];

			CSVFolder userFolder;
			try {
				userFolder = CSVActivity.fm.getSharedfolder();

				userFolder.decrypt();

				userFolder.addContent(username, this.capability);
				userFolder.encrypt();

				CSVActivity.fm.uploadObject(userFolder);
				return "Share with " + username + " created";
			} catch (ServerCommunicationException e) {
				return e.getMessage();
			}

		}
		return "This error should not happen";
	}

	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(this.caller, result, Toast.LENGTH_LONG).show();
	}
}
