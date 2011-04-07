package no.ntnu.item.csv.workers;

import java.io.File;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
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
	private Capability capability;
	private String error;

	public DownloadTask(Activity caller) {
		this.caller = caller;
	}

	public DownloadTask(Activity caller, Capability capability) {
		this.caller = caller;
		this.capability = capability;
	}

	@Override
	protected String doInBackground(String... params) {
		System.out.println("Starting download");
		String alias = params[0];

		if (this.capability == null) {
			this.capability = CSVActivity.fm.getCurrentFolder().getContents()
					.get(alias);
		}

		CSVFile file;
		try {
			file = new CSVFile(this.capability, new File("/sdcard/" + alias));
			file = CSVActivity.fm.downloadFile(file);
			System.out.println("Downloaded file");
		} catch (ServerCommunicationException e) {
			this.error = e.getMessage();
		} catch (InvalidWriteEnablerException e) {
			this.error = e.getMessage();
		} catch (ImmutableFileExistsException e) {
			this.error = e.getMessage();
		} catch (RemoteFileDoesNotExistException e) {
			this.error = e.getMessage();
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
		CharSequence contentTitle = "CSV: Download Complete";
		CharSequence contentText = "File has been downloaded to " + result;
		// Text displayed when notification view is opened
		if (this.error != null) {
			tickerText = "Error downloading file";
			contentTitle = "CSV: Error downloading";
			contentText = this.error;
		}

		int icon = R.drawable.icon;
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Context context = this.caller.getApplicationContext();

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
