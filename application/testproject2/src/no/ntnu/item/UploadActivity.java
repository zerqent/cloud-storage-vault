package no.ntnu.item;

import java.io.File;

import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.exception.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UploadActivity extends Activity {
    
	private Button bSrcFile;
	private Button bDst;
	private Button bUpload;
	private TextView displaySrcFile;
	private TextView displayDstDir;
	// Constants indicating whether an activity result comes from local or remote browsing
	private final static int LOCAL = 1;
	private final static int REMOTE = 2;
	
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
				// TODO: UPLOAD FILE HERE!
				File file = new File(displaySrcFile.getText().toString());
				try {
					TestActivity.fm.upload(file, displayDstDir.getText().toString());
					System.out.println("Uploaded "+displaySrcFile.getText().toString()+" to placement "+displayDstDir.getText().toString().substring(1));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CloudServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}
		});
    }
    

    
	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			switch (requestCode){
				case LOCAL:		displaySrcFile.setText(data.getStringExtra("FILEPATH")); break;
				case REMOTE:	displayDstDir.setText(data.getStringExtra("DIRPATH")); break;
			}		
		}
	}
}
