package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.RemoteBrowseActivity;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.CSVException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;

public class UnlinkObjectTask extends AsyncTask<String, Void, Void> {

	private RemoteBrowseActivity caller;
	private CSVFolder unlinkFromFolder;
	private String error = null;

	public UnlinkObjectTask(RemoteBrowseActivity caller) {
		this.caller = caller;
	}

	public UnlinkObjectTask(RemoteBrowseActivity caller, CSVFolder parentFolder) {
		this.unlinkFromFolder = parentFolder;
	}

	@Override
	protected void onPreExecute() {
		if (this.unlinkFromFolder == null) {
			this.unlinkFromFolder = CSVActivity.fm.getCurrentFolder();
		}

		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(String... params) {
		if (params == null || params.length < 1) {
			this.error = "No alias specified";
			return null;
		}
		String alias = params[0];
		try {
			CSVActivity.fm.unLinkAliasFromFolder(alias, this.unlinkFromFolder);
		} catch (CSVException e) {
			this.error = e.getMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (this.error == null) {
			caller.doBrowsing();
			return;
		}

		NotificationManager mNotificationManager = (NotificationManager) this.caller
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Will be displayed in the top bar before opening the notification view
		CharSequence tickerText = "Error unlinking";

		int icon = R.drawable.csv;
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Context context = this.caller.getApplicationContext();

		// Text displayed when notification view is opened
		CharSequence contentTitle = "CSV: Upload Complete";
		if (this.error != null) {
			contentTitle = "CSV: Error unlinking";
		}
		CharSequence contentText = this.error;

		PendingIntent contentIntent = PendingIntent.getActivity(this.caller, 0,
				null, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		mNotificationManager.notify(1, notification);

		System.out.println(this.error);
	}

}
