package no.ntnu.item.csv;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import no.ntnu.item.csv.guiutils.BrowseList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class LocalBrowseActivity extends ListActivity {

	public static Stack<String> location = new Stack<String>(); // Keeps track
																// of the
																// current
																// directory.
	public static List<String> files = new ArrayList<String>(); // Static list
																// containing
																// all files in
																// the current
																// directory.
	public final FilenameFilter filter = new FilenameFilter() { // File filter
																// to disable
																// browsing of
																// hidden files.
		@Override
		public boolean accept(File dir, String name) {
			return !name.startsWith(".");
		}
	};
	public static final String STORAGE_INTERNAL = "Internal Storage";
	public static final String STORAGE_EXTERNAL = "External Storage";
	public String eState; // State of external device.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startLocalBrowsing();
	}

	// Mount and display storage devices before browsing locally.
	public void startLocalBrowsing() {
		location.clear();
		files.clear();
		files.add(STORAGE_INTERNAL);

		// Mounting external storage.
		eState = Environment.getExternalStorageState();
		if (eState.equals(Environment.MEDIA_MOUNTED))
			files.add(STORAGE_EXTERNAL);

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
				File clicked;
				switch (position) {
				case 1:
					clicked = Environment.getExternalStorageDirectory();
					break;
				default:
					clicked = new File("/system");
					break;
				}
				location.push(clicked.getAbsolutePath());
				doBrowse(clicked);
			}
		});
	}

	private void doBrowse(File file) {
		files.clear();
		// Open "file" if it is a directory.
		if (file.isDirectory()) {
			// Sort list
			files.add("..");
			File[] tmp = file.listFiles(filter);
			List<String> filesInDir = new ArrayList<String>();
			List<String> dirs = new ArrayList<String>();
			for (int i = 0; i < tmp.length; i++) {
				File f = tmp[i];
				if (f.isFile()) {
					filesInDir.add(f.getName());
				} else if (f.isDirectory()) {
					dirs.add(f.getName());
				}
			}
			Collections.sort(filesInDir, String.CASE_INSENSITIVE_ORDER);
			Collections.sort(dirs, String.CASE_INSENSITIVE_ORDER);
			files.addAll(dirs);
			files.addAll(filesInDir);
			BrowseList bl = new BrowseList(getCurrentDirectory(), files);
			SimpleAdapter sa = new SimpleAdapter(this, bl.getList(),
					R.layout.filelist, new String[] { "ICON", "TEXT" },
					new int[] { R.id.browse_icon, R.id.browse_text });
			setListAdapter(sa);

			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Check direction of browsing, to correctly update the
					// location stack.
					if (files.get(position).equals(".."))
						location.pop();
					else
						location.push(files.get(position));

					// If browsing out of storage device, return to show storage
					// devices.
					if (location.size() == 0) {
						startLocalBrowsing();
					} else {
						doBrowse(new File(getCurrentDirectory()));
					}
				}
			});
			// Upload file if it is not a directory.
		} else {
			Intent intent = new Intent();
			intent.putExtra("FILEPATH", file.getAbsolutePath());
			setResult(RESULT_OK, intent);
			location.clear();
			finish();

		}
	}

	// Obtain the current directory path.
	public String getCurrentDirectory() {
		String path = "";
		for (Iterator<String> iterator = location.iterator(); iterator
				.hasNext();) {
			String type = iterator.next();
			path += type + "/";
		}
		return path;
	}

}
