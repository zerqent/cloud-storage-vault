package no.ntnu.item.csv;

import java.io.IOException;

import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewFolderActivity extends Activity {

	private Button bCancel;
	private Button bOk;
	private TextView tv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);

		bCancel = (Button) findViewById(R.id.nf_cancel);
		bOk = (Button) findViewById(R.id.nf_ok);
		
		tv = (TextView) findViewById(R.id.newFile);

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
		
		bOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if(tv.getText().toString().equals("")){
					System.out.println("No filename");
					setResult(RESULT_CANCELED, intent);
					finish();
				}else{
					try {
						CSVActivity.fm.mkdir(tv.getText().toString());
						setResult(RESULT_OK, intent);
					} catch (InsufficientPermissionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setResult(RESULT_CANCELED, intent);
					} catch (DuplicateAliasException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setResult(RESULT_CANCELED, intent);
					} catch (ServerCommunicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setResult(RESULT_CANCELED, intent);
					} catch (IllegalFileNameException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setResult(RESULT_CANCELED, intent);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setResult(RESULT_CANCELED, intent);
					}
					finish();
				}
			}
		});
		
	}
}
