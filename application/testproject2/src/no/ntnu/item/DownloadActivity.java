package no.ntnu.item;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class DownloadActivity extends ListActivity {

	public String[] listItems = {"exploring", "android", "list", "activities"};
	
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, listItems));
		setContentView(R.layout.filelist);
	}
}
