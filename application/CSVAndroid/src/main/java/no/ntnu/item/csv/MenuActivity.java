package no.ntnu.item.csv;

import no.ntnu.item.csv.credentials.LocalCredentials;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

	private Button bDownload;
	// private Button bUpload;
	private Button bShare;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Capture viewed buttons.
		bDownload = (Button) findViewById(R.id.menu_download);
		// bUpload = (Button) findViewById(R.id.menu_upload);
		bShare = (Button) findViewById(R.id.menu_share);

		// Add button listeners.
		bDownload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, RemoteBrowseActivity.class);
				startActivity(intent);
			}
		});

		// bUpload.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // DisplayCapability.displayCapability(CSVActivity.this,
		// // CSVActivity.fm.getCurrentFolder().getCapability())
		// // .show();
		// IntentIntegrator.shareText(MenuActivity.this, CSVActivity.fm
		// .getCurrentFolder().getCapability().toString());
		// }
		// });

		bShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this,
						CreateShareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// FIXME: Just for testing purposes
		menu.add("Delete Capability");
		menu.add("Exit program");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Delete Capability")) {
			deleteFile(LocalCredentials.save_file);
			finish();
			return true;
		} else if (item.getTitle().equals("Exit program")) {
			System.exit(0);
		}
		return false;
	}
}
