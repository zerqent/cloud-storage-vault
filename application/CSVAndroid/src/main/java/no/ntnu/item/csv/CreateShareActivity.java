package no.ntnu.item.csv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateShareActivity extends Activity {
	private Button bConfirmAlias;
	private EditText eAlias;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createshare);
		
		bConfirmAlias = (Button) findViewById(R.id.CreateShareConfirm);
		eAlias = (EditText) findViewById(R.id.CreateShareAlias);
		
		bConfirmAlias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), eAlias.getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
