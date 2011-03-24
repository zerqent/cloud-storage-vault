package no.ntnu.item.csv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewFolderActivity extends Activity {

	public static final String NEW_FOLDER = "NEWFOLDER";

	private Button bCancel;
	private Button bOk;
	private TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);
		bCancel = (Button) findViewById(R.id.nf_cancel);
		bOk = (Button) findViewById(R.id.nf_ok);

		tv = (TextView) findViewById(R.id.newFile);

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
				Intent intent = getIntent();
				if (tv.getText().toString().equals("")) {
					System.out.println("No filename");
					setResult(RESULT_CANCELED, intent);
					finish();
				} else {
					// setResult(RESULT_OK, tv.getText())
					intent.putExtra(NEW_FOLDER, tv.getText().toString());
					setResult(RESULT_OK, intent);
					finish();

				}
			}
		});
	}
}
