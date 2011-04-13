package no.ntnu.item.csv.credentials;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DisplayCapability {

	public static AlertDialog displayCapability(Activity activity,
			Capability capability) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Keys");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});

		String msg = "Key: (" + capability.getType().toString() + ")\n";
		msg += Base32.encode(capability.getKey()) + "\n";

		if (capability.isFolder()) {
			msg += "Verify: \n";
			msg += Base32.encode(capability.getVerificationKey());
		}

		builder.setMessage(msg);

		AlertDialog dialog = builder.create();
		dialog.setOwnerActivity(activity);
		return dialog;

	}
}
