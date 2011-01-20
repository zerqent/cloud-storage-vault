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
				Intent intent = new Intent();
				intent.setClass(TestActivity.this, DownloadActivity.class);
				startActivity(intent);
			}
		});
        
        bUpload.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(TestActivity.this, BrowseActivity.class);
				startActivityForResult(intent, 1);
			}
		});   
    }
    
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println(resultCode);
		if(resultCode == RESULT_OK){
			if(data.getStringExtra("ACTION").equals("UPLOAD")){
				String srcfilepath = data.getStringExtra("FILEPATH");
				
				System.out.println("File: "+srcfilepath+" is now uploading");
			}
		}
	}

}
