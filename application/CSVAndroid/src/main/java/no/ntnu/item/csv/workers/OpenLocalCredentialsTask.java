package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.PasswordPromptActivity;
import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.exception.IncorrectPasswordException;
import android.app.Activity;
import android.os.AsyncTask;

public class OpenLocalCredentialsTask extends
		AsyncTask<String, Void, LocalCredentials> {

	private Activity caller;

	public OpenLocalCredentialsTask(Activity caller) {
		this.caller = caller;
	}

	@Override
	protected LocalCredentials doInBackground(String... params) {

		String password;
		if (params == null || params.length < 1) {
			return null;
		} else {
			password = params[0];
		}

		try {
			return LocalCredentials.openExistingLocalCredentials(this.caller,
					password);
		} catch (IncorrectPasswordException e) {
			return null;
		}

	}

	@Override
	protected void onPostExecute(LocalCredentials result) {
		if (this.caller instanceof PasswordPromptActivity) {
			((PasswordPromptActivity) this.caller).callback(result);
		}
	}

}
