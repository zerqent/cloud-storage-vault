package no.ntnu.item.csv;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocalBrowseActivity extends ListActivity{

	public static Stack<String> location = new Stack<String>(); // Keeps track of the current directory.
	public static List<String> files = new ArrayList<String>(); // Static list containing all files in the current directory.
	public final FilenameFilter filter = new FilenameFilter(){ 	// File filter to disable browsing of hidden files.
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
	};
	public String eState; 										// State of external device.
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		startLocalBrowsing();
	}
	
	// Mount and display storage devices before browsing locally.
	public void startLocalBrowsing(){
		files.clear();
		files.add("Internal storage");
		
		// Mounting external storage.
		eState = Environment.getExternalStorageState();
		if(eState.equals(Environment.MEDIA_MOUNTED))
			files.add("External storage");
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, files));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				File clicked;
				switch (position){
					case 1: clicked = Environment.getExternalStorageDirectory(); break;
					default: clicked = new File("/system"); break;
				}
				location.push(clicked.getAbsolutePath());
				doBrowse(clicked);
			}
		});
	}

	private void doBrowse(File file){
		files.clear();
		// Open "file" if it is a directory.
		if(file.isDirectory()){
			files.add("..");
			files.addAll(Arrays.asList(file.list(filter)));
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, files));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			
			lv.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// Check direction of browsing, to correctly update the location stack.
					if(files.get(position).equals(".."))
						location.pop();
					else
						location.push(files.get(position));
					
					// If browsing out of storage device, return to show storage devices.
					if (location.size() == 0)
						startLocalBrowsing();
					else
						doBrowse(new File(getCurrentDirectory()));
				}
			});
		// Upload file if it is not a directory.
		}else{
			//TODO: new UploadTask()
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
		for (Iterator<String> iterator = location.iterator(); iterator.hasNext();) {
			String type = iterator.next();
			path += type + "/";
		}
		return path;
	}
	
}
