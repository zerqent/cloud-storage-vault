package no.ntnu.item.csv.csvobject.man;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.IllegalRootCapException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.apache.http.client.ClientProtocolException;

/**
 * @author pal
 * 
 */
public class CSVFileManager {

	public static final String SHARE_FOLDER = "SHARE_FOLDER";

	private Stack<Capability> location; // Stack storing capabilities of parent
	// directories
	private CSVFolder currentFolder; // The current folder object visited

	public CSVFileManager(Capability root_cap) throws IllegalRootCapException,
			RemoteFileDoesNotExistException, ServerCommunicationException {
		if (root_cap == null)
			throw new IllegalRootCapException();

		byte[] resp = Communication.get(root_cap.getStorageIndex(),
				Communication.SERVER_GET);

		this.location = new Stack<Capability>();

		this.currentFolder = CSVFolder.createFromByteArray(resp, root_cap);
		this.location.push(root_cap);
	}

	public void put(String filepath, CSVFolder folder) throws IOException,
			InsufficientPermissionException, IllegalFileNameException,
			DuplicateAliasException, ServerCommunicationException {
		// Check write permissions
		if (folder == null) {
			folder = this.currentFolder;
		}

		if (folder.getCapability().getType() != CapabilityType.RW) {
			throw new InsufficientPermissionException();
		}

		// Check for illegal and duplicate filename
		int ind = filepath.lastIndexOf("/");
		String filename = filepath.substring(ind + 1);

		if (filename.equals("..")) {
			throw new IllegalFileNameException("..");
		}

		if (folder.getContents().containsKey(filename)) {
			throw new DuplicateAliasException(filename);
		}

		// Create file or folder object
		File content = new File(filepath);
		CSVObject file = null;
		if (content.isDirectory()) {
			file = new CSVFolder();
		} else if (content.isFile()) {
			file = new CSVFile(content);
		} else {
			System.out.println("ERROR: Can not upload a non-existing file.");
			return;
		}
		file.encrypt();

		// Upload file
		int code = Communication.put(file, Communication.SERVER_PUT);
		if (code != 201) {
			throw new ServerCommunicationException(code);
		}

		// Insert file capability and alias into parent directory and upload
		// parent directory
		folder.getContents().put(content.getName(), file.getCapability());
		folder.encrypt();

		code = Communication.put(folder, Communication.SERVER_PUT);
		if (code != 200) {
			throw new ServerCommunicationException(code);
		}

		// If content of uploaded File is a folder: put all sub files and sub
		// folders!
		if (content.isDirectory() && file instanceof CSVFolder) {
			String[] subfiles = content.list();
			for (int i = 0; i < subfiles.length; i++)
				this.put(content.getAbsolutePath() + "/" + subfiles[i],
						(CSVFolder) file);
		}
	}

	public CSVObject get(CSVFolder folder, String alias)
			throws ClientProtocolException, IOException, NoSuchAliasException {
		Capability cap;
		if (folder == null) {
			folder = this.currentFolder;
		} else {
			System.out.println("Folder er satt.");
		}

		if ((cap = folder.getContents().get(alias)) == null) {
			throw new NoSuchAliasException(alias);
		}

		byte[] resp;
		try {
			resp = Communication.get(cap.getStorageIndex(),
					Communication.SERVER_GET);
		} catch (RemoteFileDoesNotExistException e) {
			e.printStackTrace();
			return null;
		} catch (ServerCommunicationException e) {
			e.printStackTrace();
			return null;
		}
		CSVObject file;

		if (cap.isFile()) {
			file = CSVFile.createFromByteArray(resp, cap);
		} else {
			file = CSVFolder.createFromByteArray(resp, cap);
		}
		return file;
	}

	public Map<String, Capability> ls() {
		if (this.currentFolder.getContents() == null) {
			this.currentFolder.decrypt();
		}
		return this.currentFolder.getContents();
	}

	public void cd(String folderAlias) throws ClientProtocolException,
			IOException, NoSuchAliasException {
		CSVObject folder;
		if (!folderAlias.equals("..")) {
			folder = this.get(null, folderAlias);
			if (folder == null || folder instanceof CSVFile)
				// Shouldn't really happen
				return;

			Capability current_cap = this.currentFolder.getCapability();
			if (this.location.peek() != current_cap) {
				this.location.push(current_cap);
			}
			this.currentFolder = (CSVFolder) folder;
		} else {
			// Check if we are in root dir:
			if (this.location.size() <= 1)
				return;

			byte[] resp;
			try {
				resp = Communication.get(this.location.lastElement()
						.getStorageIndex(), Communication.SERVER_GET);
			} catch (RemoteFileDoesNotExistException e) {
				e.printStackTrace();
				return;
			} catch (ServerCommunicationException e) {
				e.printStackTrace();
				return;
			}
			folder = CSVFolder.createFromByteArray(resp,
					this.location.lastElement());

			if (folder == null)
				return;

			folder.decrypt();
			this.currentFolder = (CSVFolder) folder;
			this.location.pop();
		}
	}

	public CSVFolder createNewFolder() {
		return new CSVFolder();
	}

	public void putCSVObjectIntoFolder(CSVObject csvObject, String target_dir,
			String alias) throws IllegalFileNameException,
			DuplicateAliasException, InsufficientPermissionException,
			ServerCommunicationException {

		CSVFolder target = this.currentFolder;

		if (target_dir != null && target_dir == CSVFileManager.SHARE_FOLDER) {
			Capability root_cap = this.location.firstElement();
			byte[] resp;
			try {
				resp = Communication.get(root_cap.getStorageIndex(),
						Communication.SERVER_GET);
			} catch (RemoteFileDoesNotExistException e1) {
				e1.printStackTrace();
				return;
			}

			CSVFolder root_folder = CSVFolder.createFromByteArray(resp,
					root_cap);
			root_folder.decrypt();

			Capability share_cap = null;

			try {
				share_cap = this.get(root_folder, CSVFileManager.SHARE_FOLDER)
						.getCapability();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (NoSuchAliasException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				resp = Communication.get(share_cap.getStorageIndex(),
						Communication.SERVER_GET);
			} catch (RemoteFileDoesNotExistException e) {
				e.printStackTrace();
				return;
			}

			target = CSVFolder.createFromByteArray(resp, share_cap);
			target.decrypt();
		}

		// Check write permissions
		if (target.getCapability().getType() != CapabilityType.RW) {
			throw new InsufficientPermissionException(alias);
		}
		// Check for duplicate filenames
		if (target.getContents().containsKey(alias)) {
			throw new DuplicateAliasException(alias);
		}
		// Check for illegal directory name
		if (alias.equals("..")) {
			throw new IllegalFileNameException(alias);
		}

		target.getContents().put(alias, csvObject.getCapability());
		target.encrypt();

		int code_folder = Communication.put(target, Communication.SERVER_PUT);

		if (code_folder != 200) {
			// Expected return is 200, we are updating an existing folder
			throw new ServerCommunicationException(code_folder);
		}
	}

	public void uploadObject(CSVObject object)
			throws ServerCommunicationException {
		int code = Communication.put(object, Communication.SERVER_PUT);
		if (code != 201 && code != 200) {
			throw new ServerCommunicationException(code);
		}
		return;
	}

	public void mkdir(String alias, String target_dir, CSVFolder folder)
			throws IOException, InsufficientPermissionException,
			DuplicateAliasException, ServerCommunicationException,
			IllegalFileNameException {

		uploadObject(folder);
		putCSVObjectIntoFolder(folder, target_dir, alias);

		return;
	}

	public void mkdir(String alias, String target_dir) throws IOException,
			InsufficientPermissionException, DuplicateAliasException,
			ServerCommunicationException, IllegalFileNameException {

		CSVFolder folder = createNewFolder();
		mkdir(alias, target_dir, folder);

		return;
	}

	public static void main(String[] args) throws IOException,
			IllegalRootCapException, RemoteFileDoesNotExistException,
			ServerCommunicationException {
		// Creating a root directory
		// CSVFolder root = new CSVFolder();
		// root.encrypt();
		// Communication.put(root, Communication.SERVER_PUT);
		// System.out.println(root.getCapability().toString());
		// System.out.println(root.getCapability().getStorageIndex());
		// System.exit(0);

		Capability root_cap = CapabilityImpl
				.fromString("D:RW:MDJH4ISE34ULD7RW3TGOX7NOJU:LID4JW5EQAI2QMCLNMPM7ZSNG4");
		CSVFileManager fm = new CSVFileManager(root_cap);
		System.out.println("File manager created!");
		// fm.cd("Desktop");
		// fm.ls();
		// fm.put("/home/melvold/Desktop", fm.currentFolder);
		// fm.ls();
		while (true) {
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();

			if (input.toLowerCase().equals("ls")) {
				Map<String, Capability> content = fm.ls();
				if (!content.isEmpty()) {
					System.out
							.println("File name \tStorage index \t\t\t\tCapability type");
					for (Map.Entry<String, Capability> entry : content
							.entrySet())
						System.out.println(entry.getKey() + " "
								+ entry.getValue().getStorageIndex() + " "
								+ entry.getValue().getType());
				}
			} else if (input.startsWith("cd")) {
				String tmp = input.substring(3);
				try {
					fm.cd(tmp);
				} catch (NoSuchAliasException e) {
					System.out.println(e.getMessage());
				}
			} else if (input.startsWith("put")) {
				String tmp = input.substring(4);
				try {
					fm.put(tmp, fm.currentFolder);
				} catch (InsufficientPermissionException e) {
					System.out.println(e.getMessage());
				} catch (IllegalFileNameException e) {
					System.out.println(e.getMessage());
				} catch (DuplicateAliasException e) {
					System.out.println(e.getMessage());
				} catch (ServerCommunicationException e) {
					System.out.println(e.getMessage());
				}
			} else if (input.startsWith("cat")) {
				String tmp = input.substring(4);
				CSVFile foo;
				try {
					foo = (CSVFile) fm.get(null, tmp);
					foo.decrypt();
					String bar = new String(foo.getPlainText());
					System.out.println(bar);
				} catch (NoSuchAliasException e) {
					System.out.println(e.getMessage());
				}

			} else if (input.startsWith("get")) {
				String[] tmp = input.substring(4).split(" ");
				String alias = tmp[0];
				String save_path = tmp[1];
				CSVFile foo;
				try {
					foo = (CSVFile) fm.get(null, alias);
					foo.decrypt();
					CSVFile foobar = foo;
					FileUtils.writeFileToDisk(save_path, foo.getPlainText());
					System.out.println("Wrote file to disk");
				} catch (NoSuchAliasException e) {
					System.out.println(e.getMessage());
				}

			} else if (input.startsWith("mkdir")) {
				String tmp = input.substring("mkdir".length() + 1);
				try {
					fm.mkdir(tmp, null);
				} catch (InsufficientPermissionException e) {
					System.out.println(e.getMessage());
				} catch (DuplicateAliasException e) {
					System.out.println(e.getMessage());
				} catch (ServerCommunicationException e) {
					System.out.println(e.getMessage());
				} catch (IllegalFileNameException e) {
					System.out.println(e.getMessage());
				}
			} else if (input.toLowerCase().equals("exit")) {
				System.exit(0);
			} else {
				System.out.println("You SUCK");
			}

		}

	}

	public CSVFolder getCurrentFolder() {
		return currentFolder;
	}

	public static CSVObject getCSVObject(Capability cap) {
		byte[] resp;
		try {
			resp = Communication.get(cap.getStorageIndex(),
					Communication.SERVER_GET);
		} catch (RemoteFileDoesNotExistException e) {
			e.printStackTrace();
			return null;
		} catch (ServerCommunicationException e) {
			e.printStackTrace();
			return null;
		}
		CSVObject file;

		if (cap.isFile()) {
			file = CSVFile.createFromByteArray(resp, cap);
		} else {
			file = CSVFolder.createFromByteArray(resp, cap);
		}
		return file;

	}

	public boolean inRootDir() {
		if (this.location.size() == 1)
			return true;
		return false;
	}
}
