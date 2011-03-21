package no.ntnu.item.csv;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.csvobject.man.CSVFileManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

		fm = new CSVFileManager(root_cap);

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

}
