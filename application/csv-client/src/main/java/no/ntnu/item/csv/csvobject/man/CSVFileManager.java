package no.ntnu.item.csv.csvobject.man;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
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
		}
	}

	public CSVObject putObjectIntoFolder(CSVObject object, CSVFolder folder,
			String alias) throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			DuplicateAliasException, IllegalFileNameException {
		if (alias.equals("") || alias.equals("..")) {
			throw new IllegalFileNameException();
		}

		if (folder.getContents().containsKey(alias)) {
			throw new DuplicateAliasException();
		}

		CSVObject tmpObject = uploadObject(object);

		folder.addContent(alias, object.getCapability());
		uploadFolder(folder);
		return tmpObject;
	}

	public CSVObject putObjectIntoCurrentFolder(CSVObject object, String alias)
			throws IllegalFileNameException, DuplicateAliasException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {

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
			ImmutableFileExistsException, RemoteFileDoesNotExistException {
		return (CSVFile) downloadObject(file);
	}

	public CSVFolder downloadFolder(CSVFolder folder)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException {
		return (CSVFolder) downloadObject(folder);
	}

	public CSVFolder downloadFolder(String alias, CSVFolder fromFolder)
			throws ServerCommunicationException,
			RemoteFileDoesNotExistException {
		Capability cap = fromFolder.getContents().get(alias);
		return downloadFolder(cap);
	}

	public CSVObject downloadObject(CSVObject object)
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException {

		HttpResponse response = this.connection.get(object.getCapability()
				.getStorageIndex());
		parseStatuscode(response.getStatusLine().getStatusCode());

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
		return object;
	}

	private CSVFolder downloadFolder(Capability capability)
			throws ServerCommunicationException,
			RemoteFileDoesNotExistException {
		CSVFolder folder = new CSVFolder(capability);
		try {
			folder = this.downloadFolder(folder);
		} catch (InvalidWriteEnablerException e) {
		} catch (ImmutableFileExistsException e) {
		}
		return folder;
	}

	public CSVFolder cd(String alias) throws ServerCommunicationException,
			RemoteFileDoesNotExistException, NoSuchAliasException {
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

	// private CSVFolder getFolder(Capability cap)
	// throws RemoteFileDoesNotExistException,
	// ServerCommunicationException {
	// CSVFolder folder = new CSVFolder(cap);
	// folder = (CSVFolder) this.connection.get(folder);
	// return folder;
	// }
	//
	// public CSVFolder getSharedfolder() {
	// return sharedfolder;
	// }
	//
	// public void put(String filepath, CSVFolder folder) throws IOException,
	// InsufficientPermissionException, IllegalFileNameException,
	// DuplicateAliasException, ServerCommunicationException {
	// // Check write permissions
	// if (folder == null) {
	// folder = this.currentFolder;
	// }
	//
	// if (folder.getCapability().getType() != CapabilityType.RW) {
	// throw new InsufficientPermissionException();
	// }
	//
	// // Check for illegal and duplicate filename
	// int ind = filepath.lastIndexOf("/");
	// String filename = filepath.substring(ind + 1);
	//
	// if (filename.equals("..")) {
	// throw new IllegalFileNameException("..");
	// }
	//
	// if (folder.getContents().containsKey(filename)) {
	// throw new DuplicateAliasException(filename);
	// }
	//
	// // Create file or folder object
	// File content = new File(filepath);
	// CSVObject file = null;
	// if (content.isDirectory()) {
	// file = new CSVFolder();
	// } else if (content.isFile()) {
	// file = new CSVFile(content);
	// } else {
	// System.out.println("ERROR: Can not upload a non-existing file.");
	// return;
	// }
	//
	// // Upload file
	// int code = this.connection.put(file);
	// if (code != 201) {
	// throw new ServerCommunicationException(code);
	// }
	//
	// // Insert file capability and alias into parent directory and upload
	// // parent directory
	// folder.getContents().put(content.getName(), file.getCapability());
	//
	// code = this.connection.put(folder);
	// if (code != 200) {
	// throw new ServerCommunicationException(code);
	// }
	//
	// // If content of uploaded File is a folder: put all sub files and sub
	// // folders!
	// if (content.isDirectory() && file instanceof CSVFolder) {
	// String[] subfiles = content.list();
	// for (int i = 0; i < subfiles.length; i++)
	// this.put(content.getAbsolutePath() + "/" + subfiles[i],
	// (CSVFolder) file);
	// }
	// }
	//
	// public CSVObject get(CSVFolder folder, String alias)
	// throws ClientProtocolException, IOException, NoSuchAliasException {
	// Capability cap;
	// if (folder == null) {
	// folder = this.currentFolder;
	// } else {
	// System.out.println("Folder er satt.");
	// }
	//
	// if ((cap = folder.getContents().get(alias)) == null) {
	// throw new NoSuchAliasException(alias);
	// }
	//
	// byte[] resp;
	// try {
	// resp = this.connection.get(cap.getStorageIndex());
	// } catch (RemoteFileDoesNotExistException e) {
	// e.printStackTrace();
	// return null;
	// } catch (ServerCommunicationException e) {
	// e.printStackTrace();
	// return null;
	// }
	// CSVObject file;
	//
	// if (cap.isFile()) {
	// file = CSVFile.createFromByteArray(resp, cap);
	// } else {
	// file = CSVFolder.createFromByteArray(resp, cap);
	// }
	// return file;
	// }
	//
	// public Map<String, Capability> ls() {
	// if (this.currentFolder.getContents() == null) {
	// this.currentFolder.decrypt();
	// }
	// return this.currentFolder.getContents();
	// }
	//
	// public void cd(String folderAlias) throws ClientProtocolException,
	// IOException, NoSuchAliasException {
	// CSVObject folder;
	// if (!folderAlias.equals("..")) {
	// folder = this.get(null, folderAlias);
	// if (folder == null || folder instanceof CSVFile)
	// // Shouldn't really happen
	// return;
	//
	// Capability current_cap = this.currentFolder.getCapability();
	// if (this.location.peek() != current_cap) {
	// this.location.push(current_cap);
	// }
	//
	// this.currentFolder = (CSVFolder) folder;
	// this.location.push(folder.getCapability());
	// } else {
	// // Check if we are in root dir:
	// if (this.location.size() <= 1)
	// return;
	//
	// byte[] resp;
	// try {
	// resp = this.connection.get(this.location.get(
	// this.location.size() - 2).getStorageIndex());
	// } catch (RemoteFileDoesNotExistException e) {
	// e.printStackTrace();
	// return;
	// } catch (ServerCommunicationException e) {
	// e.printStackTrace();
	// return;
	// }
	// folder = CSVFolder.createFromByteArray(resp,
	// this.location.get(this.location.size() - 2));
	//
	// if (folder == null)
	// return;
	//
	// folder.decrypt();
	// this.currentFolder = (CSVFolder) folder;
	// this.location.pop();
	// System.out.println("Performed action");
	// }
	// }
	//
	// public CSVFolder createNewFolder() {
	// return new CSVFolder();
	// }
	//
	// public void putCSVObjectIntoFolder(CSVObject csvObject, String
	// target_dir,
	// String alias) throws IllegalFileNameException,
	// DuplicateAliasException, InsufficientPermissionException,
	// ServerCommunicationException {
	//
	// CSVFolder target = this.currentFolder;
	//
	// if (target_dir != null && target_dir == CSVFileManager.SHARE_FOLDER) {
	// // this.sharedfolder.addContent(alias, csvObject.getCapability());
	// target = this.sharedfolder;
	// }
	//
	// // Check write permissions
	// if (target.getCapability().getType() != CapabilityType.RW) {
	// throw new InsufficientPermissionException(alias);
	// }
	// // Check for duplicate filenames
	// if (target.getContents().containsKey(alias)) {
	// throw new DuplicateAliasException(alias);
	// }
	// // Check for illegal directory name
	// if (alias.equals("..")) {
	// throw new IllegalFileNameException(alias);
	// }
	//
	// target.getContents().put(alias, csvObject.getCapability());
	// target.encrypt();
	//
	// int code_folder = this.connection.put(target);
	//
	// if (code_folder != 200) {
	// // Expected return is 200, we are updating an existing folder
	// throw new ServerCommunicationException(code_folder);
	// }
	// }
	//
	// public void uploadObject(CSVObject object)
	// throws ServerCommunicationException {
	// int code = this.connection.put(object);
	// if (code != 201 && code != 200) {
	// throw new ServerCommunicationException(code);
	// }
	// return;
	// }
	//
	// public CSVFolder mkdir(String alias, String target_dir, CSVFolder folder)
	// throws IOException, InsufficientPermissionException,
	// DuplicateAliasException, ServerCommunicationException,
	// IllegalFileNameException {
	//
	// uploadObject(folder);
	// putCSVObjectIntoFolder(folder, target_dir, alias);
	//
	// return folder;
	// }
	//
	// public CSVFolder mkdir(String alias, String target_dir) throws
	// IOException,
	// InsufficientPermissionException, DuplicateAliasException,
	// ServerCommunicationException, IllegalFileNameException {
	//
	// CSVFolder folder = mkdir(alias, target_dir, createNewFolder());
	//
	// return folder;
	// }
	//
	// public static void main(String[] args) throws IOException,
	// IllegalRootCapException, RemoteFileDoesNotExistException,
	// ServerCommunicationException {
	// // Creating a root directory
	// // CSVFolder root = new CSVFolder();
	// // root.encrypt();
	// // Communication.put(root, Communication.SERVER_PUT);
	// // System.out.println(root.getCapability().toString());
	// // System.out.println(root.getCapability().getStorageIndex());
	// // System.exit(0);
	//
	// Capability root_cap = CapabilityImpl
	// .fromString("D:RW:MDJH4ISE34ULD7RW3TGOX7NOJU:LID4JW5EQAI2QMCLNMPM7ZSNG4");
	// Communication conn = new Communication("http://create.q2s.ntnu.no/",
	// "foo", "bar");
	// CSVFileManager fm = new CSVFileManager(root_cap, conn);
	// System.out.println("File manager created!");
	// // fm.cd("Desktop");
	// // fm.ls();
	// // fm.put("/home/melvold/Desktop", fm.currentFolder);
	// // fm.ls();
	// while (true) {
	// Scanner sc = new Scanner(System.in);
	// String input = sc.nextLine();
	//
	// if (input.toLowerCase().equals("ls")) {
	// Map<String, Capability> content = fm.ls();
	// if (!content.isEmpty()) {
	// System.out
	// .println("File name \tStorage index \t\t\t\tCapability type");
	// for (Map.Entry<String, Capability> entry : content
	// .entrySet())
	// System.out.println(entry.getKey() + " "
	// + entry.getValue().getStorageIndex() + " "
	// + entry.getValue().getType());
	// }
	// } else if (input.startsWith("cd")) {
	// String tmp = input.substring(3);
	// try {
	// fm.cd(tmp);
	// } catch (NoSuchAliasException e) {
	// System.out.println(e.getMessage());
	// }
	// } else if (input.startsWith("put")) {
	// String tmp = input.substring(4);
	// try {
	// fm.put(tmp, fm.currentFolder);
	// } catch (InsufficientPermissionException e) {
	// System.out.println(e.getMessage());
	// } catch (IllegalFileNameException e) {
	// System.out.println(e.getMessage());
	// } catch (DuplicateAliasException e) {
	// System.out.println(e.getMessage());
	// } catch (ServerCommunicationException e) {
	// System.out.println(e.getMessage());
	// }
	// } else if (input.startsWith("cat")) {
	// String tmp = input.substring(4);
	// CSVFile foo;
	// try {
	// foo = (CSVFile) fm.get(null, tmp);
	// foo.decrypt();
	// String bar = new String(foo.getPlainText());
	// System.out.println(bar);
	// } catch (NoSuchAliasException e) {
	// System.out.println(e.getMessage());
	// }
	//
	// } else if (input.startsWith("get")) {
	// String[] tmp = input.substring(4).split(" ");
	// String alias = tmp[0];
	// String save_path = tmp[1];
	// CSVFile foo;
	// try {
	// foo = (CSVFile) fm.get(null, alias);
	// foo.decrypt();
	// FileUtils.writeFileToDisk(save_path, foo.getPlainText());
	// System.out.println("Wrote file to disk");
	// } catch (NoSuchAliasException e) {
	// System.out.println(e.getMessage());
	// }
	//
	// } else if (input.startsWith("mkdir")) {
	// String tmp = input.substring("mkdir".length() + 1);
	// try {
	// fm.mkdir(tmp, null);
	// } catch (InsufficientPermissionException e) {
	// System.out.println(e.getMessage());
	// } catch (DuplicateAliasException e) {
	// System.out.println(e.getMessage());
	// } catch (ServerCommunicationException e) {
	// System.out.println(e.getMessage());
	// } catch (IllegalFileNameException e) {
	// System.out.println(e.getMessage());
	// }
	// } else if (input.toLowerCase().equals("exit")) {
	// System.exit(0);
	// } else {
	// System.out.println("You SUCK");
	// }
	//
	// }
	//
	// }
	//
	// public CSVFolder getCurrentFolder() {
	// return currentFolder;
	// }
	//
	// public CSVObject getCSVObject(Capability cap) {
	// byte[] resp;
	// try {
	// resp = this.connection.get(cap.getStorageIndex());
	// } catch (RemoteFileDoesNotExistException e) {
	// e.printStackTrace();
	// return null;
	// } catch (ServerCommunicationException e) {
	// e.printStackTrace();
	// return null;
	// }
	// CSVObject file;
	//
	// if (cap.isFile()) {
	// file = CSVFile.createFromByteArray(resp, cap);
	// } else {
	// file = CSVFolder.createFromByteArray(resp, cap);
	// }
	// return file;
	//
	// }
	//
	// public boolean inRootDir() {
	// if (this.location.size() == 1)
	// return true;
	// return false;
	// }
	//
	// public Capability getRootCap() {
	// return this.location.get(0);
	// }
}
