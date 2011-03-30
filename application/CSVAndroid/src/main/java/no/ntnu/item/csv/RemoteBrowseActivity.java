package no.ntnu.item.csv;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.credentials.DisplayCapability;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.guiutils.BrowseList;
import no.ntnu.item.csv.workers.AddToShareTask;
import no.ntnu.item.csv.workers.CreateFolderTask;
import no.ntnu.item.csv.workers.DownloadTask;
import no.ntnu.item.csv.workers.UploadTask;

import org.apache.http.client.ClientProtocolException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
	private CreateFolderTask newFolderTask;

	private final String NEW_SHARE_ACTION = "Share with new user";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
		doBrowsing();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Share with ...");
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		for (String alias : CSVActivity.fm.getSharedfolder().getContents()
				.keySet()) {
			menu.add(alias);
		}
		menu.add(this.NEW_SHARE_ACTION);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		LinearLayout ll = (LinearLayout) info.targetView;
		String alias = ((TextView) ll.getChildAt(1)).getText().toString();

		String menu_element = item.getTitle().toString();

		if (menu_element.equals(this.NEW_SHARE_ACTION)) {
			// TODO: Make this more user friendly, aka upon result, should share
			// the folder in question.
			Intent intent = new Intent(RemoteBrowseActivity.this,
					CreateShareActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}
		AddToShareTask atst = new AddToShareTask(this);
		atst.execute(menu_element, alias);

		return super.onContextItemSelected(item);
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
			this.newFolderTask = new CreateFolderTask(this);
			this.newFolderTask.execute();

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
		files = new HashMap<String, Capability>();
		files.putAll(CSVActivity.fm.ls());
		files.put("..", null);
		BrowseList bl = new BrowseList(files);
		SimpleAdapter sa = new SimpleAdapter(this, bl.getList(),
				R.layout.filelist, new String[] { "ICON", "TEXT" }, new int[] {
						R.id.browse_icon, R.id.browse_text });
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
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (NoSuchAliasException e) {
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
				String alias = data
						.getStringExtra(NewFolderActivity.NEW_FOLDER);
				try {
					CSVFolder folder = this.newFolderTask.get();
					CreateFolderTask cft = new CreateFolderTask(this);
					cft.setFolder(folder);
					cft.execute(alias);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			default:
				;
			}
		}
	}
}
