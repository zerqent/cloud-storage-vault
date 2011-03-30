package no.ntnu.item.csv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SetPasswordActivity extends Activity {

	public static final String PASSWORD = "PASSWORD";
	private Button bCancel;
	private Button bOk;
	private TextView tv1;
	private TextView tv2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createpassword);
		bCancel = (Button) findViewById(R.id.setpassword_cancelbutton);
		bOk = (Button) findViewById(R.id.setpassword_okbutton);
		tv1 = (TextView) findViewById(R.id.setpassword_new);
		tv2 = (TextView) findViewById(R.id.setpassword_confnew);

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});

		bOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = tv1.getText().toString();
				String confPassword = tv2.getText().toString();
				// TODO: more comprehensive password rules can be implemented
				if (confPassword.equals(password) && !password.equals("")
						&& password != null && password.length() > 8) {
					Intent intent = getIntent();
					intent.putExtra(PASSWORD, tv1.getText().toString());
					setResult(RESULT_OK, intent);
					finish();
				} else {
					if (confPassword.equals(password))
						Toast.makeText(
								SetPasswordActivity.this,
								"The password must be at least 9 characters long",
								Toast.LENGTH_LONG);
					else
						Toast.makeText(SetPasswordActivity.this,
								"The password is not confirmed corretly",
								Toast.LENGTH_LONG);
				}
			}
		});
	}
}
