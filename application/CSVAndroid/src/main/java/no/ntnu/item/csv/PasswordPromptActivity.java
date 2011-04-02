package no.ntnu.item.csv;

import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.exception.IncorrectPasswordException;
import android.app.Activity;
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
				Intent intent = getIntent();
				String password = tv.getText().toString();
				if (password.equals("") || password == null) {
					Toast.makeText(getApplicationContext(), "Wrong password!",
							Toast.LENGTH_LONG).show();
				} else {
					try {
						// LocalCredentials creds = new LocalCredentials(
						// PasswordPromptActivity.this, password, false);
						LocalCredentials creds = LocalCredentials
								.openExistingLocalCredentials(
										PasswordPromptActivity.this, password);
						intent.putExtra(GetRootCapActivity.ROOTCAP, creds
								.getRootCapability().toString());
						intent.putExtra(CONFIGURE, false);
						setResult(RESULT_OK, intent);
						finish();
					} catch (IncorrectPasswordException e) {
						Toast.makeText(getApplicationContext(),
								"Wrong password!", Toast.LENGTH_LONG).show();
					}
				}

			}
		});
	}
}
