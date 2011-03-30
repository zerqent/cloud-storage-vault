package no.ntnu.item.csv.firststart;

import no.ntnu.item.csv.GetRootCapActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.SetPasswordActivity;
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

	private static final int REQUEST_ROOTCAP = 1;
	private static final int REQUEST_BARCODE = 0;
	public static final String ROOT_CAP_STRING_KEY = "rootcapstring";

	public Capability root_cap;
	public String rootCapString;

	private TextView tv;
	private Button bImportManual;
	private Button bImportBarcode;
	private Button bCreateNew;
	private Button bImport;
	private Dialog progress;

	private static final int PROGRESSBAR_GENERATE = 2;
	private static final int SET_PASSWORD_NEW_ROOT = 3;
	private static final int SET_PASSWORD_OLD_ROOT = 4;

	private ProgressDialog progressDialog;

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
				Intent intent = new Intent();
				intent.setClass(FirstStartActivity.this,
						SetPasswordActivity.class);
				startActivityForResult(intent, SET_PASSWORD_NEW_ROOT);
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

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESSBAR_GENERATE:
			this.progressDialog = new ProgressDialog(
					FirstStartActivity.this.getApplicationContext());
			this.progressDialog.setTitle("Title");
			this.progressDialog.setMessage("Message");
			return this.progressDialog;
		}
		return super.onCreateDialog(id);
	}

	private void done() {
		Intent intent = getIntent();
		if (rootCapString == null) {
			rootCapString = root_cap.toString();
		}
		intent.putExtra(GetRootCapActivity.ROOTCAP, rootCapString);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_ROOTCAP: {
			if (resultCode == RESULT_OK) {
				rootCapString = data.getStringExtra(ROOT_CAP_STRING_KEY);
				Intent intent = new Intent();
				intent.setClass(FirstStartActivity.this,
						SetPasswordActivity.class);
				startActivityForResult(intent, SET_PASSWORD_OLD_ROOT);
			}
			break;
		}
		case REQUEST_BARCODE: {
			if (resultCode == RESULT_OK) {
				rootCapString = data.getStringExtra("SCAN_RESULT");
				Intent intent = new Intent();
				intent.setClass(FirstStartActivity.this,
						SetPasswordActivity.class);
				startActivityForResult(intent, SET_PASSWORD_OLD_ROOT);
			}
			break;
		}
		case SET_PASSWORD_NEW_ROOT: {
			if (resultCode == RESULT_OK) {
				progress = ProgressDialog.show(FirstStartActivity.this, "",
						"Generating secret keys, please wait..", true);
				new CreateRootCapTask(FirstStartActivity.this,
						data.getStringExtra(SetPasswordActivity.PASSWORD))
						.execute();
			}
			return;
		}
		case SET_PASSWORD_OLD_ROOT: {
			if (resultCode == RESULT_OK) {
				new LocalCredentials(this,
						CapabilityImpl.fromString(rootCapString),
						data.getStringExtra(SetPasswordActivity.PASSWORD));
				done();
			}
			return;
		}
		}

	}

	public void supplyWithRootCap(Capability cap) {
		this.root_cap = cap;
		this.progress.dismiss();
		done();
	}

}
