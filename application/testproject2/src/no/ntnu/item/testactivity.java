package no.ntnu.item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class testactivity extends Activity
{
	private Button bDownload;
	private Button bUpload;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // capture viewed buttons
        bDownload = (Button)findViewById(R.id.download);
        bUpload = (Button)findViewById(R.id.upload);
        
        // add button listeners
        bDownload.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//System.out.println("Download");
				Intent intent = new Intent();
				intent.setClass(testactivity.this, downloadactivity.class);
				startActivity(intent);
			}
		});
        
        bUpload.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				System.out.println("Upload");
				
			}
		});
        
        
    }
}
