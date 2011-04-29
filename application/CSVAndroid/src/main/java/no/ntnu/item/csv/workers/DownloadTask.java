package no.ntnu.item.csv.workers;

import java.io.File;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.fileutils.FileUtils;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class DownloadTask extends AsyncTask<String, Void, String> {

	private Activity caller;
	private Capability capability;
	private String error;
	private NotificationManager mNotificationManager;

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

		CSVFile file = null;
		try {
			file = new CSVFile(this.capability, new File(
					Environment.getExternalStorageDirectory(), alias));
			file = CSVActivity.fm.downloadFile(file);
		} catch (ServerCommunicationException e) {
			this.error = e.getMessage();
		} catch (InvalidWriteEnablerException e) {
			this.error = e.getMessage();
		} catch (ImmutableFileExistsException e) {
			this.error = e.getMessage();
		} catch (RemoteFileDoesNotExistException e) {
			this.error = e.getMessage();
		} catch (FailedToVerifySignatureException e) {
			file.getFile().delete();
			this.error = e.getMessage();
		}

		return "/mnt/sdcard/" + alias;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		CharSequence tickerText = "File download completed";
		CharSequence contentTitle = "CSV: Download Complete";
		CharSequence contentText = "File has been downloaded to " + result;
		// Text displayed when notification view is opened
		if (this.error != null) {
			tickerText = "Error downloading file";
			contentTitle = "CSV: Error downloading";
			contentText = this.error;
		}

		int icon = R.drawable.downloaded;
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// What to do when someone touch the intent
		Intent notificationIntent = new Intent();
		notificationIntent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(result);
		String extension = FileUtils.getFileExtension(result);
		notificationIntent.setDataAndType(Uri.fromFile(file), MimeTypeMap
				.getSingleton().getMimeTypeFromExtension(extension));
		PendingIntent contentIntent = PendingIntent.getActivity(
				this.caller.getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(this.caller.getApplicationContext(),
				contentTitle, contentText, contentIntent);
		mNotificationManager.notify(1, notification);

		this.caller = null;
		System.out.println("download complete");
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

		this.mNotificationManager = (NotificationManager) this.caller
				.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.downloaded;
		CharSequence tickerText = "Downloading file";
		CharSequence contentTitle = "CSV: File Download";
		CharSequence contentText = "A file download is in progress";

		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this.caller, 0,
				null, 0);
		notification.setLatestEventInfo(this.caller.getApplicationContext(),
				contentTitle, contentText, contentIntent);
		mNotificationManager.notify(1, notification);

		return;

	}

}
