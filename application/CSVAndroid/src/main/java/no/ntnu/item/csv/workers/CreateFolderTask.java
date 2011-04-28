package no.ntnu.item.csv.workers;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.RemoteBrowseActivity;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;

public class CreateFolderTask extends AsyncTask<String, Void, CSVFolder> {

	private Activity caller;
	private String error = null;
	private CSVFolder parentFolder = null;
	private CSVFolder createdFolder = null;

	public CreateFolderTask(Activity caller) {
		this.caller = caller;
	}

	public CreateFolderTask(Activity caller, CSVFolder parentfolder,
			CSVFolder putFolder) {
		this.caller = caller;
		this.parentFolder = parentfolder;
		this.createdFolder = putFolder;
	}

	public CreateFolderTask(Activity caller, CSVFolder parentFolder) {
		this.caller = caller;
		this.parentFolder = parentFolder;
	}

	public void setParentFolder(CSVFolder parentFolder) {
		this.parentFolder = parentFolder;
	}

	public void setCreatedFolder(CSVFolder folder) {
		this.createdFolder = folder;
	}

	public CSVFolder getCreatedFolder() {
		return this.createdFolder;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// CharSequence text = "Creating folder";
		// int duration = Toast.LENGTH_SHORT;
		//
		// Toast toast = Toast.makeText(this.caller.getApplicationContext(),
		// text,
		// duration);
		// toast.show();
	}

	@Override
	protected CSVFolder doInBackground(String... params) {

		if (params.length == 0 || params[0] == null) {
			System.out.println("Lager bare keys");
			// CSVFolder folder = CSVActivity.fm.createNewFolder();
			CSVFolder folder = new CSVFolder();
			try {
				folder = CSVActivity.fm.uploadFolder(folder);
			} catch (ServerCommunicationException e) {
				this.error = e.getMessage();
			} catch (InvalidWriteEnablerException e) {
				this.error = e.getMessage();
			} catch (ImmutableFileExistsException e) {
				this.error = e.getMessage();
			}
			return folder;
		}

		String alias = params[0];

		if (this.createdFolder == null) {
			this.createdFolder = new CSVFolder();
		}

		try {
			if (this.parentFolder == null) {
				CSVActivity.fm.putObjectIntoCurrentFolder(this.createdFolder,
						alias);
			} else {
				CSVActivity.fm.putObjectIntoFolder(this.createdFolder,
						this.parentFolder, alias);
			}
			System.out.println("Uploaded file");
		} catch (InsufficientPermissionException e) {
			this.error = e.getMessage();
		} catch (IllegalFileNameException e) {
			this.error = e.getMessage();
		} catch (DuplicateAliasException e) {
			this.error = e.getMessage();
		} catch (ServerCommunicationException e) {
			this.error = e.getMessage();
		} catch (InvalidWriteEnablerException e) {
			this.error = e.getMessage();
		} catch (ImmutableFileExistsException e) {
			this.error = e.getMessage();
		}
		return this.createdFolder;
	}

	@Override
	protected void onPostExecute(CSVFolder folder) {

		// We notify iv something goes wrong
		if (this.error != null) {
			NotificationManager mNotificationManager = (NotificationManager) this.caller
					.getSystemService(Context.NOTIFICATION_SERVICE);

			// Will be displayed in the top bar before opening the notification
			// view
			CharSequence tickerText = "Error creating folder";

			int icon = R.drawable.uploaded;
			Notification notification = new Notification(icon, tickerText,
					System.currentTimeMillis());
			notification.flags = Notification.FLAG_AUTO_CANCEL;

			Context context = this.caller.getApplicationContext();

			// Text displayed when notification view is opened
			CharSequence contentTitle = "CSV: Error creating folder";
			CharSequence contentText = this.error;

			PendingIntent contentIntent = PendingIntent.getActivity(
					this.caller, 0, null, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);
			mNotificationManager.notify(1, notification);
		}

		if (caller instanceof RemoteBrowseActivity) {
			RemoteBrowseActivity ac = (RemoteBrowseActivity) caller;
			ac.doBrowsing();
		}

		this.caller = null;
	}
}
