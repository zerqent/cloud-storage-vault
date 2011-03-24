package no.ntnu.item.csv.workers;

import java.io.IOException;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.RemoteBrowseActivity;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.firststart.FirstStartActivity;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class CreateFolderTask extends AsyncTask<String, Void, CSVFolder> {

	private Activity caller;
	private String error = null;
	private CSVFolder folder;

	public CreateFolderTask(Activity caller) {
		this.caller = caller;
	}

	public void setFolder(CSVFolder folder) {
		this.folder = folder;
	}

	public CSVFolder getFolder() {
		return this.folder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		CharSequence text = "Creating folder";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(this.caller.getApplicationContext(), text,
				duration);
		toast.show();
	}

	@Override
	protected CSVFolder doInBackground(String... params) {
		if (params.length == 0 || params[0] == null) {
			CSVFolder folder = CSVActivity.fm.createNewFolder();
			return folder;
		}

		String alias = params[0];
		try {
			if (this.folder != null) {
				CSVActivity.fm.mkdir(alias, this.folder);
			} else {
				CSVActivity.fm.mkdir(alias);
			}
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
		} catch (IllegalFileNameException e) {
			this.error = e.getMessage();
		} catch (DuplicateAliasException e) {
			this.error = e.getMessage();
		} catch (ServerCommunicationException e) {
			this.error = e.getMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(CSVFolder folder) {

		// We notify iv something goes wrong
		if (this.error != null) {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) this.caller
					.getSystemService(ns);

			// Will be displayed in the top bar before opening the notification
			// view
			CharSequence tickerText = "Error creating folder";
			CharSequence contentTitle = "CSV: Error creating folder";
			CharSequence contentText = this.error;
			int icon = R.drawable.icon;
			Notification notification = new Notification(icon, tickerText,
					System.currentTimeMillis());
			Context context = this.caller.getApplicationContext();
			Intent notificationIntent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager.notify(1, notification);
		}

		if (caller instanceof RemoteBrowseActivity) {
			RemoteBrowseActivity ac = (RemoteBrowseActivity) caller;
			ac.doBrowsing();
		} else if (caller instanceof FirstStartActivity) {
			FirstStartActivity foo = (FirstStartActivity) caller;
		}

		this.caller = null;
	}
}
