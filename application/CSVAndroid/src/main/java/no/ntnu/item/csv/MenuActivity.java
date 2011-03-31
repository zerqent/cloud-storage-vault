package no.ntnu.item.csv;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.firststart.FirstStartActivity;
import no.ntnu.item.csv.workers.ImportShareTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

	private Button bDownload;
	private Button bConf;
	private Button bShare;
	private Button bImportShare;

	private static final int MENU_IMPORTSHARE = 0;
	private static final int REQUEST_BARCODEIMPORT = 1;
	private static final int REQUEST_MANUALIMPORT = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Capture viewed buttons.
		bDownload = (Button) findViewById(R.id.menu_download);
		bConf = (Button) findViewById(R.id.menu_conf);
		bShare = (Button) findViewById(R.id.menu_share);
		bImportShare = (Button) findViewById(R.id.menu_shareimport);

		// Add button listeners.
		bDownload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, RemoteBrowseActivity.class);
				startActivity(intent);
			}
		});

		bShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this,
						CreateShareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});

		bImportShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(MENU_IMPORTSHARE);
			}
		});

		bConf.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, FirstStartActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Log out");
		menu.add("Exit program");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case MENU_IMPORTSHARE: {
			final CharSequence[] items = { "From Barcode", "Manual" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose import method");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					Intent intent;
					switch (item) {
					case 0:
						intent = new Intent(
								"com.google.zxing.client.android.SCAN");
						intent.setPackage("com.google.zxing.client.android");
						intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
						startActivityForResult(intent, REQUEST_BARCODEIMPORT);
						break;
					case 1:
						intent = new Intent();
						intent.setClass(MenuActivity.this,
								ManualImportShareActivity.class);
						startActivityForResult(intent, REQUEST_MANUALIMPORT);
						break;
					}
				}
			});
			return builder.create();
		}
		default: {
			break;
		}
		}
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Log out")) {
			System.exit(0);
		} else if (item.getTitle().equals("Exit program")) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_BARCODEIMPORT:
			if (resultCode == RESULT_OK) {
				String tmp = data.getStringExtra("SCAN_RESULT");
				String username = tmp.split(":")[0];
				String stringCap = tmp.substring(username.length() + 1);
				Capability capability = CapabilityImpl.fromString(stringCap);
				ImportShareTask ist = new ImportShareTask(this, capability);
				ist.execute(username);
			}
			return;
		case REQUEST_MANUALIMPORT:
			if (resultCode == RESULT_OK) {
				String username = data
						.getStringExtra(ManualImportShareActivity.REQUEST_RESULT_USERNAME);
				String capabilityString = data
						.getStringExtra(ManualImportShareActivity.REQUEST_RESULT_CAPABILITY);
				Capability capability = CapabilityImpl
						.fromString(capabilityString);
				ImportShareTask ist = new ImportShareTask(this, capability);
				ist.execute(username);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
