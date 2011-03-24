package no.ntnu.item.csv;

import java.io.IOException;
import java.util.Map;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.credentials.DisplayCapability;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.guiutils.BrowseList;
import no.ntnu.item.csv.workers.CreateFolderTask;
import no.ntnu.item.csv.workers.DownloadTask;
import no.ntnu.item.csv.workers.UploadTask;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RemoteBrowseActivity extends ListActivity {

	public static final int MENU_UPLOAD_FILE = 1;
	public static final int MENU_CREATE_FOLDER = 2;
	public static final int MENU_SHOW_CAPABILITY = 3;

	private static Map<String, Capability> files;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doBrowsing();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_UPLOAD_FILE, 0, "Upload File");
		menu.add(0, MENU_CREATE_FOLDER, 0, "Create Folder");
		menu.add(0, MENU_SHOW_CAPABILITY, 0, "Show cap");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();

		switch (item.getItemId()) {
		case MENU_CREATE_FOLDER:
			intent.setClass(this, NewFolderActivity.class);
			startActivityForResult(intent, MENU_CREATE_FOLDER);
			return true;

		case MENU_UPLOAD_FILE:
			intent.setClass(this, LocalBrowseActivity.class);
			startActivityForResult(intent, MENU_UPLOAD_FILE);
			return true;
		case MENU_SHOW_CAPABILITY:
			DisplayCapability.displayCapability(this,
					CSVActivity.fm.getCurrentFolder().getCapability()).show();
			intent = null;
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void doBrowsing() {
		files = CSVActivity.fm.ls();
		// files.put("..", null);

		// List<String> tmpList = new ArrayList<String>();
		// tmpList.addAll(files.keySet());
		// Collections.sort(tmpList);
		// tmpList.add(0, "..");
		// setListAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.test_list_item, tmpList));
		// System.out.print("Done");

		BrowseList bl = new BrowseList(files);
		SimpleAdapter sa = new SimpleAdapter(this, bl.getList(),
				android.R.layout.activity_list_item, new String[] { "TEXT",
						"ICON" }, new int[] { android.R.id.text1,
						android.R.id.icon });
		setListAdapter(sa);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LinearLayout ll = (LinearLayout) view;
				TextView tmp = (TextView) ll.getChildAt(1);
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
					} catch (NoSuchAliasException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					new DownloadTask(RemoteBrowseActivity.this).execute(alias);
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case MENU_UPLOAD_FILE:
				new UploadTask(this).execute(data.getStringExtra("FILEPATH"));
				break;
			case MENU_CREATE_FOLDER:
				new CreateFolderTask(this).execute(data
						.getStringExtra(NewFolderActivity.NEW_FOLDER));

			default:
				;
			}
		}
	}
}
