package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class AddToShareTask extends AsyncTask<String, Void, Boolean> {
	private final Activity caller;

	public AddToShareTask(Activity caller) {
		this.caller = caller;
	}

	private String error;

	@Override
	protected Boolean doInBackground(String... params) {
		String user, alias;
		user = params[0];
		alias = params[1];

		Capability cap = CSVActivity.fm.getCurrentFolder().getContents()
				.get(alias);

		CSVFolder userFolder;
		try {
			userFolder = CSVActivity.fm.downloadFolder(user,
					CSVActivity.fm.getShareFolder());

			userFolder.addContent(alias, cap);

			CSVActivity.fm.uploadFolder(userFolder);

			return true;
		} catch (ServerCommunicationException e) {
			this.error = e.getMessage();
		} catch (RemoteFileDoesNotExistException e) {
			this.error = e.getMessage();
		} catch (InvalidWriteEnablerException e) {
			this.error = e.getMessage();
		} catch (ImmutableFileExistsException e) {
			this.error = e.getMessage();
		} catch (FailedToVerifySignatureException e) {
			this.error = e.getMessage();
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Toast.makeText(this.caller, "Folder added to share",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this.caller, "Error adding share: " + this.error,
					Toast.LENGTH_LONG).show();
		}
	}
}
