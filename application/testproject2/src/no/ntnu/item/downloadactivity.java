package no.ntnu.item;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class downloadactivity extends ListActivity {

	public String[] listItems = {"exploring", "android", "list", "activities"};
	
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        System.out.println("DOWNZLOAD");
        //setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems));
		//setContentView(R.layout.filelist);
	}
}
