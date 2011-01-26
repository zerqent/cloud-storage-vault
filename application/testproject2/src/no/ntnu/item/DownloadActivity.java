package no.ntnu.item;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

public class DownloadActivity extends ListActivity {
	
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		Intent intent = new Intent();
		intent.setClass(DownloadActivity.this, BrowseActivity.class);
		intent.putExtra("browse location", "remote");
		intent.putExtra("purpose", "download");
		startActivityForResult(intent, 1);
	}
	
	
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			System.out.println("Download and open "+data.getStringExtra("DOWNLOADFILE"));
		}
		// TODO: DOWNLOAD FILE HERE!
		
		finish();
	}
}
