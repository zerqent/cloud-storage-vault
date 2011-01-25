package no.ntnu.item;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.provider.amazons3.AmazonS3FileManager;
import no.ntnu.item.provider.amazons3.AmazonS3Provider;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BrowseActivity extends ListActivity{

	public static List<String> files = new ArrayList<String>(); // Static list containing all files in the current directory.
	public String eState; 										// State of external device.
	public static Stack<String> location = new Stack<String>(); // Keeps track of the current directory.
	public final FilenameFilter filter = new FilenameFilter(){ 	// File filter to disable browsing of hidden files.
			public boolean accept(File dir, String name) {
				return !name.startsWith(".");
			}
	};
	public Intent browseIntent;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		browseIntent = getIntent();
		if(browseIntent.getStringExtra("browse location").equals("local")){
			initializeLocalBrowsing();
		}else{
			try {
				System.out.println("Starting remote browsing for "+browseIntent.getStringExtra("purpose"));
				initializeRemoteBrowsing();
			} catch (CloudServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// Mount and display storage devices.
	public void initializeLocalBrowsing(){
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
				uploadLocalFile(clicked);
			}
		});
	}
	
	// Open "file" if it is a directory. Upload "file" if it is not a directory.
	public void uploadLocalFile(File file){
		files.clear();
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
					
					File clicked = new File(getCurrentDirectory());
					
					// If browsing out of storage device, return to show storage devices.
					if (location.size() == 0)
						initializeLocalBrowsing();
					else
						uploadLocalFile(clicked);
				}
			});
		}else{
			Intent intent = new Intent();
			intent.putExtra("FILEPATH", file.getAbsolutePath());
			setResult(RESULT_OK, intent);
			location.clear();
			finish();
		}
	}
	
	// Start browsing for destination directory in cloud
	public void initializeRemoteBrowsing() throws CloudServiceException{
		files.clear();
		location.clear(); // Kan fjernes når updatefunksjonen er ferdig
		AmazonS3Provider provider = new AmazonS3Provider();
		AmazonS3FileManager fm = new AmazonS3FileManager(provider);
		
		location.push(provider.getDefaultBucket());
		files.addAll(fm.ls());
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, files));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				File clicked;
//				switch (position){
//					case 1: clicked = Environment.getExternalStorageDirectory(); break;
//					default: clicked = new File("/system"); break;
//				}
//				location.push(clicked.getAbsolutePath());
//				uploadLocalFile(clicked);
				System.out.println("clicked on file");
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("LONG CLICK!");
				return false;
			}
			
		});
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
