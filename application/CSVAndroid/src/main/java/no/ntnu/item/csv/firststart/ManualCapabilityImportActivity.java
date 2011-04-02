package no.ntnu.item.csv.firststart;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManualCapabilityImportActivity extends Activity {

	private static final int DIALOG_VERIFY_KEY = 1;

	private EditText key;
	private Button processButon;
	private String verifyHash;
	private Capability rootCap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manualcapability);

		this.key = (EditText) findViewById(R.id.manualcapability_keyedittext);
		this.processButon = (Button) findViewById(R.id.manualcapability_processbutton);

		this.processButon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				byte[] encKey = Base32.decode(key.getText().toString());
				rootCap = new CapabilityImpl(CapabilityType.RW, encKey, null,
						false);
				CSVFolder folder;
				try {
					folder = (CSVFolder) CSVActivity.fm.getCSVObject(rootCap);
					if (folder == null) {
						Toast.makeText(ManualCapabilityImportActivity.this,
								"The requested root capability does not exist",
								Toast.LENGTH_LONG).show();
					} else {
						verifyHash = Base32.encode(folder.getPublicKeyHash());
						showDialog(DIALOG_VERIFY_KEY);
					}
				} catch (FailedToVerifySignatureException e) {
					Toast.makeText(ManualCapabilityImportActivity.this,
							"The requested root folder could not be verified",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_VERIFY_KEY:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Is the following key correct?:\n"
					+ this.verifyHash);
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getIntent();
							intent.putExtra(
									FirstStartActivity.ROOT_CAP_STRING_KEY,
									rootCap.toString() + ":" + verifyHash);
							setResult(RESULT_OK, intent);
							dismissDialog(DIALOG_VERIFY_KEY);
							finish();
						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(DIALOG_VERIFY_KEY);
						}
					});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}
