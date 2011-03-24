package no.ntnu.item.csv.firststart;

import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.credentials.LocalCredentials;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstStartActivity extends Activity {

	public static final int REQUEST_ROOTCAP = 1;

	public Capability root_cap;
	public String rootCapString = null;

	private TextView tv;
	private Button bImportManual;
	private Button bImportBarcode;
	private TextView header;
	private Button bCreateNew;
	private Button bImport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firststart);

		// Capture viewed buttons.
		bCreateNew = (Button) findViewById(R.id.firstStart_new);
		bImport = (Button) findViewById(R.id.firstStart_import);
		header = (TextView) findViewById(R.id.firstStart_header);

		bImportManual = (Button) findViewById(R.id.firstStart_importmanual);
		bImportBarcode = (Button) findViewById(R.id.firstStart_importbarcode);
		tv = (TextView) findViewById(R.id.firstStart_importheader);

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
				// header.setVisibility(View.INVISIBLE);
				// bImport.setVisibility(View.INVISIBLE);
				bImport.setEnabled(false);
				// bCreateNew.setVisibility(View.INVISIBLE);
				bCreateNew.setEnabled(false);

				tv.setVisibility(View.VISIBLE);
				bImportManual.setEnabled(true);
				bImportManual.setVisibility(View.VISIBLE);
				bImportBarcode.setEnabled(true);
				bImportBarcode.setVisibility(View.VISIBLE);

			}
		});

		bImportManual.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(FirstStartActivity.this,
						ManualCapabilityImportActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		bImportBarcode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO: Figure out barcode support.
			}
		});

	}

	private void done() {
		Intent intent = getIntent();
		if (rootCapString == null) {
			rootCapString = root_cap.toString();
		}
		intent.putExtra("rootcap", rootCapString);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ROOTCAP:
			rootCapString = data.getStringExtra("rootcapstring");
			done();
			return;
		}

	}
}
