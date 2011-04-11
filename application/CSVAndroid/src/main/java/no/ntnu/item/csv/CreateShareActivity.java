package no.ntnu.item.csv;

import java.util.concurrent.ExecutionException;

import no.ntnu.item.csv.contrib.com.google.zxing.integration.android.IntentIntegrator;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.workers.CreateFolderTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateShareActivity extends Activity {
	private Button bConfirmAlias;
	private EditText eAlias;
	private CreateFolderTask cft;

	public static String REQUEST_RESULT_USERALIAS = "usersharecap";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createshare);

		// Start to generate keys
		this.cft = new CreateFolderTask(this);
		this.cft.execute();

		bConfirmAlias = (Button) findViewById(R.id.CreateShareConfirm);
		eAlias = (EditText) findViewById(R.id.CreateShareAlias);

		bConfirmAlias.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CSVFolder folder = null;
				String alias = eAlias.getText().toString();
				CreateFolderTask completefolder = new CreateFolderTask(
						CreateShareActivity.this, CSVActivity.fm
								.getShareFolder());
				try {
					// Will lock UI-thread if keys are not ready, but the
					// work should be done.
					folder = cft.get();
					completefolder.setCreatedFolder(folder);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

				// Put folder with alias in the share folder
				completefolder.execute(alias);

				String qr_str = CSVActivity.connection.getUsername() + ":"
						+ folder.getCapability().toString();
				IntentIntegrator.shareText(CreateShareActivity.this, qr_str);

				if (getCallingActivity() != null) {
					Intent intent = getIntent();
					intent.putExtra(REQUEST_RESULT_USERALIAS, eAlias.getText()
							.toString());
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}
}
