package no.ntnu.item.csv.workers;

import java.io.IOException;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.RemoteBrowseActivity;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.ServerCommunicationException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadTask extends AsyncTask<String, Void, String> {

	private Activity caller;
	private String error = null;

	public UploadTask(Activity caller) {
		this.caller = caller;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		CharSequence text = "Starting upload";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this.caller.getApplicationContext(), text,
				duration);
		toast.show();
	}

	@Override
	protected String doInBackground(String... params) {
		System.out.println("Starting upload");

		String localPath = params[0];
		try {
			CSVActivity.fm.put(localPath, null);
			System.out.println("Uploaded file");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return null;
		} catch (InsufficientPermissionException e) {
			this.error = e.getMessage();
			return this.error;
		} catch (IllegalFileNameException e) {
			this.error = e.getMessage();
			return this.error;
		} catch (DuplicateAliasException e) {
			this.error = e.getMessage();
			return this.error;
		} catch (ServerCommunicationException e) {
			this.error = e.getMessage();
			return this.error;
		}

		return "File has been uploaded";
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.caller
				.getSystemService(ns);

		// Will be displayed in the top bar before opening the notification view
		CharSequence tickerText = "File upload completed";
		if (this.error != null) {
			tickerText = "Error uploading";
		}

		int icon = R.drawable.icon;
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Context context = this.caller.getApplicationContext();

		// Text displayed when notification view is opened
		CharSequence contentTitle = "CSV: Upload Complete";
		if (this.error != null) {
			contentTitle = "CSV: Upload error";
		}
		CharSequence contentText = result;

		PendingIntent contentIntent = PendingIntent.getActivity(this.caller, 0,
				null, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		mNotificationManager.notify(1, notification);

		if (caller instanceof RemoteBrowseActivity) {
			RemoteBrowseActivity ac = (RemoteBrowseActivity) caller;
			ac.doBrowsing();
		}
		this.caller = null;
		return;
	}
}
