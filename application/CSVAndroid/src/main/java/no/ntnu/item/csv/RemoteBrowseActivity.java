package no.ntnu.item.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.workers.DownloadTask;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RemoteBrowseActivity extends ListActivity {

	private static Map<String, Capability> files;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doBrowsing();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Create folder");
		menu.add("Upload File");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Ugly
		if (item.getTitle().equals("Create folder")) {
			long before = System.currentTimeMillis();
			try {
				CSVActivity.fm.mkdir("test22");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long after = System.currentTimeMillis();
			System.out.println(after - before);
			return true;

		} else if (item.getTitle().equals("Upload File")) {
			long before = System.currentTimeMillis();
			try {
				CSVActivity.fm.put("/mnt/sdcard/test.mp3", null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long after = System.currentTimeMillis();
			System.out.println(after - before);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void doBrowsing() {
		files = CSVActivity.fm.ls();
		// files.put("..", null);

		List<String> tmpList = new ArrayList<String>();
		tmpList.addAll(files.keySet());
		Collections.sort(tmpList);
		tmpList.add(0, "..");

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.test_list_item, tmpList));
		System.out.print("Done");

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tmp = (TextView) view;
				String alias = tmp.getText().toString();
				Capability cap = files.get(alias);
				if (alias.equals("..") || cap.isFolder()) {
					try {
						CSVActivity.fm.cd(alias);
						doBrowsing();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// CSVFile foo = (CSVFile) CSVActivity.fm.get(alias);
					// FileUtils.writeFileToDisk("/mnt/sdcard/" + alias,
					// foo.getPlainText());
					new DownloadTask().execute(alias);

				}
			}
		});

	}
}
