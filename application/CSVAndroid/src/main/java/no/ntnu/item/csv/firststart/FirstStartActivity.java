package no.ntnu.item.csv.firststart;

import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.localcredentials.LocalCredentials;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstStartActivity extends Activity {

	public Capability root_cap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firststart);

		// Capture viewed buttons.
		Button bCreateNew = (Button) findViewById(R.id.firstStart_new);
		Button bImport = (Button) findViewById(R.id.firstStart_import);

		// Add button listeners.
		bCreateNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocalCredentials creds = new LocalCredentials(
						getApplicationContext(), true);
				root_cap = creds.getRootCapability();
				done();

			}
		});

		bImport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO:
				// Intent intent = new Intent();
				// // intent.setClass(CSVActivity.this, UploadActivity.class);
				// startActivity(intent);
			}
		});

	}

	private void done() {
		Intent intent = getIntent();
		intent.putExtra("rootcap", root_cap.toString());
		setResult(RESULT_OK, intent);
		finish();
	}

}
