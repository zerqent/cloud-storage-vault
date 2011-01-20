package no.ntnu.item;

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

public class BrowseActivity extends ListActivity{

	public static List<String> files = new ArrayList<String>();
	public String eState;
	public static Stack<String> location = new Stack<String>();
	public final FilenameFilter filter = new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
	};
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		files.clear();
		// mounting external storage
		eState = Environment.getExternalStorageState();
		
		if(!eState.equals(Environment.MEDIA_MOUNTED)){
			// can give different error messages from states explained in http://developer.android.com/reference/android/os/Environment.html
			System.out.println("ERROR: Could not mount SD card.");
		}else{
			System.out.println("Mounted SD card.");
			File sd = Environment.getExternalStorageDirectory();
			files.addAll(Arrays.asList(sd.list(filter)));
			
			location.push(sd.getAbsolutePath());
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, files));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			
			lv.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					File clicked = new File(getCurrentDirectory()+files.get(position));
					location.push(files.get(position));
					System.out.println("Top of stack: "+location.lastElement());
					System.out.println("In directory: "+clicked.getAbsolutePath());
					uploadFile(clicked);
				}
			});
		}
		
	}
	// opens a chosen directory or uploads a chosen file
	public void uploadFile(File file){
		files.clear();
		if(file.isDirectory()){
			// disabling ".." at mount point
			if(!file.getPath().equals("/mnt/sdcard"))
				files.add("..");
			
			files.addAll(Arrays.asList(file.list(filter)));
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, files));
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			
			lv.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// checking direction of browsing
					if(files.get(position).equals(".."))
						location.pop();
					else
						location.push(files.get(position));
					File clicked = new File(getCurrentDirectory());
					uploadFile(clicked);
				}
			});
		}else{
			Intent intent = new Intent();
			intent.putExtra("FILEPATH", file.getAbsolutePath());
			intent.putExtra("ACTION", "UPLOAD");
			setResult(RESULT_OK, intent);
			location.clear();
			finish();
		}
	}
	// obtain the current directory path
	public String getCurrentDirectory() {
		String path = "";
		for (Iterator<String> iterator = location.iterator(); iterator.hasNext();) {
			String type = iterator.next();
			path += type + "/";
		}
		return path;
	}
	
}
