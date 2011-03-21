package no.ntnu.item.csv;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.csvobject.man.CSVFileManager;
import no.ntnu.item.csv.firststart.FirstStartActivity;
import no.ntnu.item.csv.localcredentials.LocalCredentials;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class CSVActivity extends Activity {
	/** Called when the activity is first created. */

	public String myString = "foobar";
	private Button bDownload;
	private Button bUpload;
	private Button bShare;
	public static CSVFileManager fm;

	// File manager enabling remote browsing in cloud
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String root_uri = "D:RW:N6F4G3YQONT4RDHX6FQJIAKTJE:6KLHZH7URKTU7I6RL5CNEEL7QA";
		Capability root_cap = CapabilityImpl.fromString(root_uri);
		// String root_uri = "D:RW:MDJH4ISE34ULD7RW3TGOX7NOJU:LID4JW5EQAI2QMCLNMPM7ZSNG4";
		// Capability root_cap = CapabilityImpl.fromString(root_uri);

		LocalCredentials creds = new LocalCredentials(
				this.getApplicationContext(), false);
		if (creds.isFirstStart()) {
			Intent intent = new Intent();
			intent.setClass(this, FirstStartActivity.class);
			startActivityForResult(intent, 1);
		} else {
			fm = new CSVFileManager(creds.getRootCapability());
		}

		// Capture viewed buttons.
		bDownload = (Button) findViewById(R.id.menu_download);
		bUpload = (Button) findViewById(R.id.menu_upload);
		bShare = (Button) findViewById(R.id.menu_share);

		// Add button listeners.
		bDownload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(CSVActivity.this, RemoteBrowseActivity.class);
				startActivity(intent);
			}
		});

		bUpload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(CSVActivity.this, RemoteBrowseActivity.class);
				startActivity(intent);
			}
		});

		bShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setClass(TestActivity.this,
				// RemoteBrowseActivity.class);
				// startActivityForResult(intent, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO: Modify this if we wait for other results
		String root = (String) data.getExtras().get("rootcap");
		Capability rootcap = CapabilityImpl.fromString(root);
		fm = new CSVFileManager(rootcap);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// FIXME: Just for testing purposes
		menu.add("Delete Capability");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Delete Capability")) {
			deleteFile(LocalCredentials.save_file);
			finish();
			return true;
		}
		return false;
	}

}
