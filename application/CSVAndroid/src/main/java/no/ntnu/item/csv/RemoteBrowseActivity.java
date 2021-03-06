package no.ntnu.item.csv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.credentials.DisplayCapability;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.guiutils.BrowseList;
import no.ntnu.item.csv.workers.AddToShareTask;
import no.ntnu.item.csv.workers.CreateFolderTask;
import no.ntnu.item.csv.workers.DownloadTask;
import no.ntnu.item.csv.workers.UnlinkObjectTask;
import no.ntnu.item.csv.workers.UploadTask;
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
	public static final int CONTEXTMENU_DOWNLOAD_FILE = 3;
	public static final int CONTEXTMENU_SHARE_OBJECT = 1;
	public static final int CONTEXTMENU_UNLINK = 2;

	private static Map<String, Capability> files;
	private CreateFolderTask newFolderTask;

	private final String NEW_SHARE_ACTION = "Share with new user";
	private final int REQUEST_NEWUSERSHARE = 0;
	private final String REQUEST_RESULT_FOLDERALIAS = "folderalias";

	private String contextMenusAreStupidAliasHolder = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
		doBrowsing();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		LinearLayout ll = (LinearLayout) info.targetView;
		String clicked_alias = ((TextView) ll.getChildAt(1)).getText()
				.toString();
		Capability cap = CSVActivity.fm.getCurrentFolder().getContents()
				.get(clicked_alias);

		menu.setHeaderTitle("Actions");
		if (cap.isFile()) {
			menu.add(0, CONTEXTMENU_DOWNLOAD_FILE, 0, "Download File");
		}
		menu.add(0, CONTEXTMENU_UNLINK, 0, "Unlink");

		Menu sub = menu.addSubMenu("Share with...");
		for (String alias : CSVActivity.fm.getShareFolder().getContents()
				.keySet()) {
			sub.add(0, CONTEXTMENU_SHARE_OBJECT, 0, alias);
		}
		sub.add(0, CONTEXTMENU_SHARE_OBJECT, 0, this.NEW_SHARE_ACTION);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String menu_element = item.getTitle().toString();
		if (info != null) {
			LinearLayout ll = (LinearLayout) info.targetView;
			String alias = ((TextView) ll.getChildAt(1)).getText().toString();
			this.contextMenusAreStupidAliasHolder = alias;
		}

		switch (item.getItemId()) {
		case CONTEXTMENU_DOWNLOAD_FILE:
			new DownloadTask(RemoteBrowseActivity.this)
					.execute(this.contextMenusAreStupidAliasHolder);
			return true;
		case CONTEXTMENU_UNLINK:
			UnlinkObjectTask uot = new UnlinkObjectTask(this);
			uot.execute(this.contextMenusAreStupidAliasHolder);
			return true;

		case CONTEXTMENU_SHARE_OBJECT:
			if (menu_element.equals(this.NEW_SHARE_ACTION)) {
				Intent intent = new Intent(RemoteBrowseActivity.this,
						CreateShareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.putExtra(REQUEST_RESULT_FOLDERALIAS,
						this.contextMenusAreStupidAliasHolder);
				startActivityForResult(intent, REQUEST_NEWUSERSHARE);
			} else {
				AddToShareTask atst = new AddToShareTask(this);
				atst.execute(menu_element,
						this.contextMenusAreStupidAliasHolder);
				this.contextMenusAreStupidAliasHolder = null;
			}

		default:
			break;
		}

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
		files.putAll(CSVActivity.fm.getCurrentFolder().getContents());
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
					} catch (NoSuchAliasException e) {
						e.printStackTrace();
					} catch (ServerCommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteFileDoesNotExistException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FailedToVerifySignatureException e) {
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
				String alias = data
						.getStringExtra(NewFolderActivity.NEW_FOLDER);
				try {
					CSVFolder folder = this.newFolderTask.get();
					CreateFolderTask cft = new CreateFolderTask(this,
							CSVActivity.fm.getCurrentFolder(), folder);
					cft.execute(alias);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			case REQUEST_NEWUSERSHARE:
				String folderalias = data
						.getStringExtra(REQUEST_RESULT_FOLDERALIAS);
				String user = data
						.getStringExtra(CreateShareActivity.REQUEST_RESULT_USERALIAS);
				if (folderalias != null && user != null) {
					AddToShareTask atst = new AddToShareTask(this);
					atst.execute(user, folderalias);
				}

			default:
				;
			}
		}
	}
}
