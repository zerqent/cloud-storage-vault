package no.ntnu.item.csv.workers;

import java.io.IOException;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;

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
		}

		return "/mnt/sdcard/" + alias;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

}
