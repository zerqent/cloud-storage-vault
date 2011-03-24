package no.ntnu.item.csv;

import java.util.concurrent.ExecutionException;

import no.ntnu.item.csv.workers.CreateFolderTask;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateShareActivity extends Activity {
	private Button bConfirmAlias;
	private EditText eAlias;
	private CreateFolderTask cft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createshare);

		this.cft = new CreateFolderTask(this);
		this.cft.execute();

		bConfirmAlias = (Button) findViewById(R.id.CreateShareConfirm);
		eAlias = (EditText) findViewById(R.id.CreateShareAlias);

		bConfirmAlias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), eAlias.getText(),
						Toast.LENGTH_SHORT).show();

				CreateFolderTask completefolder = new CreateFolderTask(
						CreateShareActivity.this);
				try {
					// Will lock UI-thread, but the work should be done.
					completefolder.setFolder(cft.get());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				completefolder.execute(eAlias.getText().toString());
			}
		});
	}
}
