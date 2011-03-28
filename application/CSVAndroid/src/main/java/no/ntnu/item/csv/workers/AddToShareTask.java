package no.ntnu.item.csv.workers;

import java.io.IOException;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.exception.ServerCommunicationException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class AddToShareTask extends AsyncTask<String, Void, Boolean> {
	private final Activity caller;

	public AddToShareTask(Activity caller) {
		this.caller = caller;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String user, alias;
		user = params[0];
		alias = params[1];

		Capability cap = CSVActivity.fm.getCurrentFolder().getContents()
				.get(alias);

		CSVFolder userFolder;
		try {
			userFolder = (CSVFolder) CSVActivity.fm.get(
					CSVActivity.fm.getSharedfolder(), user);
			userFolder.decrypt();

			userFolder.addContent(alias, cap);
			userFolder.encrypt();

			CSVActivity.fm.uploadObject(userFolder);

			return true;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (NoSuchAliasException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServerCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Toast.makeText(this.caller, "Folder added to share",
					Toast.LENGTH_LONG).show();
		}
	}
}
