package no.ntnu.item;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UploadActivity extends Activity {
    
	public Button bSrcFile;
	public Button bDst;
	public Button bUpload;
	public TextView displaySrcFile;
	public TextView displayDstDir;
	public final static int LOCAL = 1;
	public final static int REMOTE = 2;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        
        bSrcFile = (Button)findViewById(R.id.chooseSrc);
        displaySrcFile = (TextView)findViewById(R.id.chosenSrc);
        
        bDst = (Button)findViewById(R.id.chooseDst);
        displayDstDir = (TextView)findViewById(R.id.chosenDst);
        
        bUpload = (Button)findViewById(R.id.upload);
        
        // Local browsing
        bSrcFile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UploadActivity.this, BrowseActivity.class);
				intent.putExtra("browse location", "local");
				startActivityForResult(intent, LOCAL);
			}
		});
        
        // Remote browsing
        bDst.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UploadActivity.this, BrowseActivity.class);
				intent.putExtra("browse location", "remote");
				intent.putExtra("purpose", "upload");
				startActivityForResult(intent, REMOTE);
			}
		});
        
        bUpload.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Start uploading here!
			}
		});
    }
    

    
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			if(requestCode == LOCAL){
				displaySrcFile.setText(data.getStringExtra("FILEPATH"));
			}
			if(requestCode == REMOTE){
				displayDstDir.setText(data.getStringExtra("DIRPATH"));
			}
		}
	}
}
