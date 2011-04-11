package no.ntnu.item.csv;

import java.io.File;

import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.firststart.FirstStartActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class GetRootCapActivity extends Activity {

	public static final String ROOTCAP = "ROOTCAP";
	private static final int CONFIGURE = 1;
	private static final int PASSWORD_PROMPT = 2;
	private static final int ONLINE_PASSWORD_PROMPT = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File storageFile = this.getFileStreamPath(LocalCredentials.save_file);
		if (storageFile.exists()) {
			Intent intent = new Intent();
			intent.setClass(this, PasswordPromptActivity.class);
			startActivityForResult(intent, PASSWORD_PROMPT);
		} else {
			Intent intent = new Intent();
			intent.setClass(this, EnterOnlineCredentialsActivity.class);
			startActivityForResult(intent, ONLINE_PASSWORD_PROMPT);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = new Intent();
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ONLINE_PASSWORD_PROMPT:
				intent = data;
				intent.setClass(this, FirstStartActivity.class);
				startActivityForResult(intent, CONFIGURE);
				break;
			case PASSWORD_PROMPT: {

				if (data.getBooleanExtra(PasswordPromptActivity.CONFIGURE,
						false)) {
					intent.setClass(this, FirstStartActivity.class);
					startActivityForResult(intent, CONFIGURE);
				} else {
					intent.putExtra(ROOTCAP, data.getStringExtra(ROOTCAP));
					setResult(RESULT_OK, intent);
					finish();
				}
				break;
			}
			case CONFIGURE: {
				intent.putExtra(ROOTCAP, data.getStringExtra(ROOTCAP));
				setResult(RESULT_OK, intent);
				finish();
				break;
			}
			}
		}
		if (resultCode == RESULT_CANCELED) {
			switch (requestCode) {
			case ONLINE_PASSWORD_PROMPT:
			case PASSWORD_PROMPT:
				finish();
				break;
			case CONFIGURE:
				File storageFile = this
						.getFileStreamPath(LocalCredentials.save_file);
				if (storageFile.exists()) {
					intent.setClass(this, PasswordPromptActivity.class);
					startActivityForResult(intent, PASSWORD_PROMPT);
				} else {
					finish();
				}
				break;
			}
		}
	}
}
