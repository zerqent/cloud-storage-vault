package no.ntnu.item.csv.workers;

import java.io.IOException;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		System.out.println("Starting download");
		// String localFilename = params[0];
		String alias = params[0];
		CSVFile file;
		try {
			file = (CSVFile) CSVActivity.fm.get(alias);
			System.out.println("Downloaded file");
			FileUtils.writeFileToDisk("/mnt/sdcard/" + alias,
					file.getCipherText());
			System.out.println("Wrote file to disk");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NoSuchAliasException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "/mnt/sdcard/" + alias;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		// Context ctx = Context.this.getApplicationContext();
		super.onPostExecute(result);
		CharSequence text = "Hello toast!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(null, text, duration);
		toast.show();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

}
