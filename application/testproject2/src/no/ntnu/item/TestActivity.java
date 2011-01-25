package no.ntnu.item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity
{
	private Button bDownload;
	private Button bUpload;
	private Button bShare;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Capture viewed buttons.
        bDownload = (Button)findViewById(R.id.menu_download);
        bUpload = (Button)findViewById(R.id.menu_upload);
        bShare = (Button)findViewById(R.id.menu_share);
        
        // Add button listeners.
        bDownload.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this, DownloadActivity.class);
				startActivity(intent);
			}
		});
        
        bUpload.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this, UploadActivity.class);
				startActivity(intent);
			}
		});
        
        bShare.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(TestActivity.this, RemoteBrowseActivity.class);
//				startActivityForResult(intent, 1);
			}
		}); 
    }

}
