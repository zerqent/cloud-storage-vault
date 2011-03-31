package no.ntnu.item.csv;

import no.ntnu.item.csv.credentials.LocalCredentials;
import no.ntnu.item.csv.firststart.FirstStartActivity;
import android.app.Activity;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Capture viewed buttons.
		bDownload = (Button) findViewById(R.id.menu_download);
		bConf = (Button) findViewById(R.id.menu_conf);
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

		bShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this,
						CreateShareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
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
