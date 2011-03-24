package no.ntnu.item.csv.firststart;

import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.workers.CreateRootCapTask;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstStartActivity extends Activity {

	public static final int REQUEST_ROOTCAP = 1;
	public static final int REQUEST_BARCODE = 0;

	public Capability root_cap;
	public String rootCapString = null;

	private TextView tv;
	private Button bImportManual;
	private Button bImportBarcode;
	private Button bCreateNew;
	private Button bImport;
	private Dialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firststart);

		bCreateNew = (Button) findViewById(R.id.firstStart_new);
		bImport = (Button) findViewById(R.id.firstStart_import);

		bImportManual = (Button) findViewById(R.id.firstStart_importmanual);
		bImportBarcode = (Button) findViewById(R.id.firstStart_importbarcode);
		tv = (TextView) findViewById(R.id.firstStart_importheader);

		bCreateNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progress = ProgressDialog.show(FirstStartActivity.this, "",
						"Generating secret keys, please wait..", true);
				new CreateRootCapTask(FirstStartActivity.this).execute();
			}
		});

		bImport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				bImport.setEnabled(false);
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
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.setPackage("com.google.zxing.client.android");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, REQUEST_BARCODE);
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
			new LocalCredentials(this, CapabilityImpl.fromString(rootCapString));
			done();
			return;
		case REQUEST_BARCODE:
			if (resultCode == RESULT_OK) {
				rootCapString = data.getStringExtra("SCAN_RESULT");
				new LocalCredentials(this,
						CapabilityImpl.fromString(rootCapString));
				done();
			}
		}

	}

	public void supplyWithRootCap(Capability cap) {
		this.root_cap = cap;
		new LocalCredentials(this, cap);
		this.progress.dismiss();
		done();
	}

}
