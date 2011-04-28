package no.ntnu.item.csv;

import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.workers.OpenLocalCredentialsTask;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordPromptActivity extends Activity {

	public static final String CONFIGURE = "CONFIGURE";

	private Button bConf;
	private Button bOk;
	private TextView tv;
	private Dialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enterpassword);
		bConf = (Button) findViewById(R.id.password_confbutton);
		bOk = (Button) findViewById(R.id.password_okbutton);
		tv = (TextView) findViewById(R.id.password_edittext);

		bConf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra(CONFIGURE, true);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		bOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = tv.getText().toString();
				progressDialog = ProgressDialog.show(
						PasswordPromptActivity.this, "", "Unlocking keyring..",
						true);
				OpenLocalCredentialsTask olct = new OpenLocalCredentialsTask(
						PasswordPromptActivity.this);
				olct.execute(password);
			}
		});
	}

	public void callback(LocalCredentials localCredentials) {
		if (localCredentials != null) {
			Intent intent = getIntent();

			intent.putExtra(GetRootCapActivity.ROOTCAP, localCredentials
					.getRootCapability().toString());
			intent.putExtra(CONFIGURE, false);
			setResult(RESULT_OK, intent);
			this.progressDialog.dismiss();
			finish();
		} else {
			this.progressDialog.dismiss();
			Toast.makeText(getApplicationContext(), "Wrong password!",
					Toast.LENGTH_LONG).show();
		}
	}

}
