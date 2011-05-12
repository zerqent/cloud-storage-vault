package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.MenuActivity;
import no.ntnu.item.csv.filemanager.CSVFileManager;
import no.ntnu.item.csv.foldertest.TestSpeedOfFolders;
import android.app.Activity;
import android.os.AsyncTask;

public class Benchmarkworker extends AsyncTask<Void, Void, Void> {

	private TestSpeedOfFolders test;
	private Activity caller;
	private String s;

	public Benchmarkworker(Activity caller, CSVFileManager manager) {
		test = new TestSpeedOfFolders(manager);
		this.caller = caller;
	}

	@Override
	protected Void doInBackground(Void... params) {
		this.s = test.getIt();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		MenuActivity ac = (MenuActivity) caller;
		ac.foo(this.s);
	}

}
