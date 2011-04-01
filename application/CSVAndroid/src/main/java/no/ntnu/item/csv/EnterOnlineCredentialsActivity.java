package no.ntnu.item.csv;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterOnlineCredentialsActivity extends Activity {

	public static String REQUEST_RESPONSE_USERNAME;
	public static String REQUEST_RESPONSE_PASSWORD;

	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button okButton;
	private Button cancelButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enteronlinecredentials);

		usernameEditText = (EditText) findViewById(R.id.enteronlinecredentials_username);
		passwordEditText = (EditText) findViewById(R.id.enteronlinecredentials_password);
		okButton = (Button) findViewById(R.id.enteronlinecredentials_ok);
		cancelButton = (Button) findViewById(R.id.enteronlinecredentials_cancel);

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = usernameEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				if (credentialsAreValid(username, password)) {
					Intent intent = getIntent();
					intent.putExtra(REQUEST_RESPONSE_USERNAME, username);
					intent.putExtra(REQUEST_RESPONSE_PASSWORD, password);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(EnterOnlineCredentialsActivity.this,
							"Username and/or password is invalid",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

	}

	private boolean credentialsAreValid(String username, String password) {

		if (username.length() < 3 || password.length() < 3) {
			return false;
		}
		CSVActivity.connection.setPassword(password);
		CSVActivity.connection.setUsername(username);

		try {
			return CSVActivity.connection.testLogin();
		} catch (ClientProtocolException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}