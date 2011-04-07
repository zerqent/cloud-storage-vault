package no.ntnu.item.csv;

import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.man.CSVFileManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CSVActivity extends Activity {

	public static final int GET_ROOTCAP = 1;
	public static final int MENU = 2;
	public static CSVFileManager fm;
	public static Communication connection = new Communication(
			"create.q2s.ntnu.no");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// deleteFile(LocalCredentials.save_file);
		// System.exit(0);

		if (initiated()) {
			Intent intent = new Intent();
			intent.setClass(CSVActivity.this, MenuActivity.class);
			startActivityForResult(intent, MENU);
		} else {
			Intent intent = new Intent();
			intent.setClass(CSVActivity.this, GetRootCapActivity.class);
			startActivityForResult(intent, GET_ROOTCAP);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case GET_ROOTCAP: {
				// try {
				fm = new CSVFileManager(CapabilityImpl.fromString(data
						.getStringExtra(GetRootCapActivity.ROOTCAP)),
						connection);
				Intent intent = new Intent();
				intent.setClass(CSVActivity.this, MenuActivity.class);
				startActivityForResult(intent, MENU);
				// } catch (IllegalRootCapException e) {
				// Toast.makeText(
				// getApplicationContext(),
				// "You have used an illegal root capability. Please use a valid capability.",
				// Toast.LENGTH_LONG).show();
				// Intent intent = new Intent();
				// intent.setClass(CSVActivity.this, GetRootCapActivity.class);
				// startActivityForResult(intent, GET_ROOTCAP);
				// e.printStackTrace();
				// } catch (RemoteFileDoesNotExistException e) {
				// Toast.makeText(
				// getApplicationContext(),
				// "The requested root folder does not exist on this server.",
				// Toast.LENGTH_LONG).show();
				// Intent intent = new Intent();
				// intent.setClass(CSVActivity.this, GetRootCapActivity.class);
				// startActivityForResult(intent, GET_ROOTCAP);
				// e.printStackTrace();
				// } catch (ServerCommunicationException e) {
				// Toast.makeText(
				// getApplicationContext(),
				// "An unknown error occured. Please check your configuration.",
				// Toast.LENGTH_LONG).show();
				// Intent intent = new Intent();
				// intent.setClass(CSVActivity.this, GetRootCapActivity.class);
				// startActivityForResult(intent, GET_ROOTCAP);
				// e.printStackTrace();
				// }
				break;
			}
			}
		}
		if (resultCode == RESULT_CANCELED) {
			System.out.println("SYSTEM EXIT11011");
			System.exit(0);
		}
	}

	public static boolean initiated() {
		if (fm == null) {
			return false;
		} else if (fm.getRootFolder() == null) {
			return false;
		}
		return true;
	}

}
