package no.ntnu.item.csv.csvobject.man;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

/**
 * @author pal
 * 
 */
public class CSVFileManager {

	public static final String SHARE_FOLDER = "SHARE_FOLDER";

	private final Communication connection;
	private final Stack<CSVFolder> location;

	private CSVFolder sharedfolder;

	public CSVFileManager(Communication connection) {
		this.connection = connection;
		this.location = new Stack<CSVFolder>();

	}

	public CSVFileManager(Capability capability, Communication connection) {
		this.connection = connection;
		this.location = new Stack<CSVFolder>();
		this.setRootCapability(capability);

	}

	public void setRootCapability(Capability capability) {
		CSVFolder root;
		try {
			root = downloadFolder(capability);
			this.location.push(root);

			if (!root.getContents().containsKey(SHARE_FOLDER)) {
				CSVFolder shareFolder = new CSVFolder();
				putObjectIntoFolder(shareFolder, this.location.get(0),
						SHARE_FOLDER);
				this.sharedfolder = shareFolder;
			} else {
				this.sharedfolder = downloadFolder(SHARE_FOLDER,
						this.location.get(0));
			}

		} catch (ServerCommunicationException e) {
			e.printStackTrace();
		} catch (RemoteFileDoesNotExistException e) {
			e.printStackTrace();
		} catch (InvalidWriteEnablerException e) {
		} catch (ImmutableFileExistsException e) {
		} catch (DuplicateAliasException e) {
		} catch (IllegalFileNameException e) {
		} catch (InsufficientPermissionException e) {
		} catch (FailedToVerifySignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CSVObject putObjectIntoFolder(CSVObject object, CSVFolder folder,
			String alias) throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			DuplicateAliasException, IllegalFileNameException,
			InsufficientPermissionException {
		if (alias.equals("") || alias.equals("..")) {
			throw new IllegalFileNameException();
		}

		if (folder.getContents().containsKey(alias)) {
			throw new DuplicateAliasException();
		}

		if (folder.getCapability().getType() != CapabilityType.RW) {
			throw new InsufficientPermissionException();
		}

		CSVObject tmpObject = uploadObject(object);

		folder.addContent(alias, object.getCapability());
		uploadFolder(folder);
		return tmpObject;
	}

	public CSVObject putObjectIntoCurrentFolder(CSVObject object, String alias)
			throws IllegalFileNameException, DuplicateAliasException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, InsufficientPermissionException {

		CSVFolder currentFolder = this.location.peek();
		return putObjectIntoFolder(object, currentFolder, alias);

	}

	public CSVFile uploadFile(CSVFile file)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {
		return (CSVFile) uploadObject(file);
	}

	public CSVFolder uploadFolder(CSVFolder folder)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {
		return (CSVFolder) uploadObject(folder);
	}

	private CSVObject uploadObject(CSVObject object)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {
		int code;
		if (object instanceof CSVFolder) {
			CSVFolder folder = (CSVFolder) object;
			code = this.connection
					.putByteArray(folder.getCapability().getStorageIndex()
							+ "/" + folder.getCapability().getWriteEnabler(),
							folder.upload());
		} else {
			CSVFile file = (CSVFile) object;
			code = this.connection.putInputStream(file.getCapability()
					.getStorageIndex(), file.upload(), file.getContentLength());
			file.finishedUpload();
		}

		try {
			parseStatuscode(code);
		} catch (RemoteFileDoesNotExistException e) {
		}
		return object;
	}

	private void parseStatuscode(int code) throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			RemoteFileDoesNotExistException {
		switch (code) {
		case 200:
			return;
		case 201:
			return;
		case 401:
			// Wrong write enabler
			throw new InvalidWriteEnablerException();
		case 403:
			// Immutable file already exist
			throw new ImmutableFileExistsException();
		case 404:
			throw new RemoteFileDoesNotExistException();
		default:
			throw new ServerCommunicationException(code);
		}
	}

	public CSVFile downloadFile(CSVFile file)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException,
			FailedToVerifySignatureException {
		return (CSVFile) downloadObject(file);
	}

	public CSVFolder downloadFolder(CSVFolder folder)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException,
			FailedToVerifySignatureException {
		return (CSVFolder) downloadObject(folder);
	}

	public CSVFolder downloadFolder(String alias, CSVFolder fromFolder)
			throws ServerCommunicationException,
			RemoteFileDoesNotExistException, FailedToVerifySignatureException {
		Capability cap = fromFolder.getContents().get(alias);
		return downloadFolder(cap);
	}

	public CSVObject downloadObject(CSVObject object)
			throws ServerCommunicationException,
			RemoteFileDoesNotExistException, FailedToVerifySignatureException {

		HttpResponse response = this.connection.get(object.getCapability()
				.getStorageIndex());
		try {
			parseStatuscode(response.getStatusLine().getStatusCode());
		} catch (InvalidWriteEnablerException e1) {
		} catch (ImmutableFileExistsException e1) {
		}

		HttpEntity entity = response.getEntity();

		byte[] bytes;
		try {
			if (object instanceof CSVFolder) {
				InputStream is;
				is = entity.getContent();

				int len = Integer.parseInt(response.getFirstHeader(
						"Content-Length").getValue());
				bytes = new byte[len];

				int nb;
				for (int i = 0; (nb = is.read()) != -1; i++) {
					bytes[i] = (byte) nb;
				}
				CSVFolder folder = (CSVFolder) object;
				folder.download(bytes);
				object = folder;

			} else if (object instanceof CSVFile) {
				CSVFile file = (CSVFile) object;
				if (entity != null) {
					OutputStream os = file.download();
					entity.writeTo(os);
					os.flush();
					os.close();
					file.finishedDownload();
					object = file;
				}
			}
		} catch (IOException e) {
			throw new ServerCommunicationException();
		}
		if (object.getCapability().getVerificationKey() != null
				&& !object.isValid()) {
			throw new FailedToVerifySignatureException();
		}

		return object;
	}

	public CSVFolder downloadFolder(Capability capability)
			throws ServerCommunicationException,
			RemoteFileDoesNotExistException, FailedToVerifySignatureException {
		CSVFolder folder = new CSVFolder(capability);
		try {
			folder = this.downloadFolder(folder);
		} catch (InvalidWriteEnablerException e) {
		} catch (ImmutableFileExistsException e) {
		}
		return folder;
	}

	public CSVFolder cd(String alias) throws ServerCommunicationException,
			RemoteFileDoesNotExistException, NoSuchAliasException,
			FailedToVerifySignatureException {
		if (alias.equals("..")) {
			this.location.pop();
			return this.location.peek();
		}

		if (!this.location.peek().getContents().containsKey(alias)) {
			throw new NoSuchAliasException(alias);
		}
		Capability capability = this.location.peek().getContents().get(alias);
		CSVFolder folder = downloadFolder(capability);
		this.location.push(folder);
		return folder;
	}

	public CSVFolder getShareFolder() {
		return this.sharedfolder;
	}

	public CSVFolder getCurrentFolder() {
		return this.location.peek();
	}

	public CSVFolder getRootFolder() {
		if (this.location.size() > 0) {
			return this.location.get(0);
		}
		return null;
	}
}
