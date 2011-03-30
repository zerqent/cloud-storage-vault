package no.ntnu.item.csv.workers;

import java.io.File;
import java.io.IOException;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Void, String> {

	private Activity caller;

	public DownloadTask(Activity caller) {
		this.caller = caller;
	}

	@Override
	protected String doInBackground(String... params) {
		System.out.println("Starting download");
		String alias = params[0];
		CSVFile file;
		try {
			file = (CSVFile) CSVActivity.fm.get(null, alias);
			System.out.println("Downloaded file");
			FileUtils.writeFileToDisk("/sdcard/" + alias, file.getPlainText());
			System.out.println("Wrote file to disk");
			file = null;
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
		super.onPostExecute(result);
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.caller
				.getSystemService(ns);

		// Will be displayed in the top bar before opening the notification view
		CharSequence tickerText = "File download completed";
		int icon = R.drawable.icon;
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Context context = this.caller.getApplicationContext();

		// Text displayed when notification view is opened
		CharSequence contentTitle = "CSV: Download Complete";
		CharSequence contentText = "File has been downloaded to " + result;

		// What to do when someone touch the intent
		Intent notificationIntent = new Intent();
		notificationIntent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(result);
		// TODO: Have to figure out how to get correct MimeType.
		notificationIntent.setDataAndType(Uri.fromFile(file), "audio/*");
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		mNotificationManager.notify(1, notification);

		this.caller = null;
		return;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		CharSequence text = "Starting download";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this.caller.getApplicationContext(), text,
				duration);
		toast.show();
	}

}
