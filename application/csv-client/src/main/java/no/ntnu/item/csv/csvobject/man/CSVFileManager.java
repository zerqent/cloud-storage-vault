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
import no.ntnu.item.csv.fileutils.FileUtils;

import org.apache.http.client.ClientProtocolException;

public class CSVFileManager {

	private Stack<Capability> location; // Stack storing capabilities of parent
										// directories
	private CSVFolder currentFolder; // The current folder object visited

	public CSVFileManager(Capability root_cap) {
		byte[] resp = Communication.get(root_cap.getStorageIndex(),
				Communication.SERVER_GET);

		if (resp == null)
			return;

		this.location = new Stack<Capability>();
		this.currentFolder = CSVFolder.createFromByteArray(resp, root_cap);
	}

	public void put(String filepath, CSVFolder folder) throws IOException {
		// Check write permissions
		if (folder == null) {
			folder = this.currentFolder;
		}

		if (folder.getCapability().getType() != CapabilityType.RW) {
			System.out
					.println("ERROR: You do not have permission to write to this directory.");
			return;
		}

		// Check for illegal and duplicate filename
		int ind = filepath.lastIndexOf("/");
		String filename = filepath.substring(ind + 1);

		if (filename.equals("..")) {
			System.out.println("ERROR: \"..\" is an illegal filename");
			return;
		}

		if (folder.getContents().containsKey(filename)) {
			System.out
					.println("ERROR: A file with the same name already exists. Please rename your file before uploading it.");
			return;
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
		switch (Communication.put(file, Communication.SERVER_PUT)) {
		case 201:
			System.out.println("File successfully uploaded.");
			break;
		case 400:
			System.out.println("ERROR: 400 - Bad Request.");
			return;
		case 500:
			System.out.println("ERROR: 500 - Internal server error.");
			return;
		default:
			System.out.println("ERROR: An unexpected error occured.");
			return;
		}

		// Insert file capability and alias into parent directory and upload
		// parent directory
		folder.getContents().put(content.getName(), file.getCapability());
		folder.encrypt();

		switch (Communication.put(folder, Communication.SERVER_PUT)) {
		case 200:
			System.out.println("Parent Folder successfully changed.");
			break;
		case 400:
			System.out.println("ERROR: 400 - Bad Request.");
			return;
		case 500:
			System.out.println("ERROR: 500 - Internal server error.");
			return;
		default:
			System.out.println("ERROR: An unexpected error occured.");
			return;
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

	public CSVObject get(String alias) throws ClientProtocolException,
			IOException {
		Capability cap;
		boolean bar = this.currentFolder.getContents().containsKey(alias);

		if ((cap = this.currentFolder.getContents().get(alias)) == null) {
			System.out.println("ERROR: File " + alias + " does not exist.");
			return null;
		}

		byte[] resp = Communication.get(cap.getStorageIndex(),
				Communication.SERVER_GET);
		CSVObject file;
		// switch (resp[0]) {
		// case 1:
		// file = CSVFolder.createFromByteArray(resp, cap);
		// break;
		// case 0:
		// file = CSVFile.createFromByteArray(resp, cap);
		// break;
		// default:
		// file = null;
		if (cap.isFile()) {
			file = CSVFile.createFromByteArray(resp, cap);
		} else {
			file = CSVFolder.createFromByteArray(resp, cap);
		}
		return file;
	}

	public Map<String, Capability> ls() {
		this.currentFolder.decrypt();
		return this.currentFolder.getContents();
		// if (!content.isEmpty()) {
		// // System.out
		// // .println("File name \tStorage index \t\t\t\tCapability type");
		// // for (Map.Entry<String, Capability> entry : content.entrySet())
		// // System.out.println(entry.getKey() + " "
		// // + entry.getValue().getStorageIndex() + " "
		// // + entry.getValue().getType());
		// }
	}

	public void cd(String folderAlias) throws ClientProtocolException,
			IOException {
		CSVObject folder;
		if (!folderAlias.equals("..")) {
			folder = this.get(folderAlias);
			if (folder == null || folder instanceof CSVFile)
				return;
			this.location.push(this.currentFolder.getCapability());
			this.currentFolder = (CSVFolder) folder;
		} else {
			if (this.location.size() <= 0)
				return;
			byte[] resp = Communication.get(this.location.lastElement()
					.getStorageIndex(), Communication.SERVER_GET);
			folder = CSVFolder.createFromByteArray(resp,
					this.location.lastElement());
			if (folder == null)
				return;
			Capability cap = folder.getCapability();
			folder.decrypt();
			this.currentFolder = (CSVFolder) folder;
			this.location.pop();
		}
	}

	public void mkdir(String alias) throws IOException {
		// Check that the user is within a directory
		if (this.currentFolder == null) {
			return;
		}
		// Check write permissions
		if (this.currentFolder.getCapability().getType() != CapabilityType.RW) {
			System.out
					.println("ERROR: You do not have permission to write to this directory.");
			return;
		}
		// Check for duplicate filenames
		if (this.currentFolder.getContents().containsKey(alias)) {
			System.out
					.println("ERROR: A file with the same name already exists. Please rename your folder before uploading it.");
			return;
		}
		// Check for illegal directory name
		if (alias.equals("..")) {
			System.out.println("ERROR: \"..\" is an illegal directory name");
			return;
		}

		CSVFolder folder = new CSVFolder();
		folder.encrypt();

		// Upload folder
		switch (Communication.put(folder, Communication.SERVER_PUT)) {
		case 201:
			System.out
					.println("Folder " + alias + " was successfully created.");
			break;
		case 400:
			System.out.println("ERROR: 400 - Bad Request.");
			return;
		case 500:
			System.out.println("ERROR: 500 - Internal server error.");
			return;
		case 600:
			System.out
					.println("ERROR: Missing server address, or Trying to upload an empty folder.");
			return;
		case 700:
			System.out
					.println("ERROR: You do not have permission to write to this folder.");
			return;
		default:
			System.out.println("ERROR: An unexpected error occured.");
			return;
		}

		// Insert folder capability and alias into parent directory and upload
		// parent directory
		this.currentFolder.getContents().put(alias, folder.getCapability());
		this.currentFolder.encrypt();

		switch (Communication.put(this.currentFolder, Communication.SERVER_PUT)) {
		case 200:
			System.out.println("Parent Folder successfully changed.");
			break;
		case 400:
			System.out.println("ERROR: 400 - Bad Request.");
			return;
		case 500:
			System.out.println("ERROR: 500 - Internal server error.");
			return;
		case 600:
			System.out
					.println("ERROR: Missing server address, or Trying to upload an empty folder.");
			return;
		case 700:
			System.out
					.println("ERROR: You do not have permission to write to this folder.");
			return;
		default:
			System.out.println("ERROR: An unexpected error occured.");
			return;
		}

	}

	public static void main(String[] args) throws IOException {
		// Creating a root directory
		// CSVFolder root = new CSVFolder();
		// root.encrypt();
		// Communication.put(root, Communication.SERVER_PUT);
		// System.out.println(root.getCapability().toString());
		// System.out.println(root.getCapability().getStorageIndex());
		// System.exit(0);
		Capability root_cap = CapabilityImpl
				.fromString("D:RW:HEKYPXHSSDY2D6Q5JJ2XKTBEKQ:PKNUQN6YE2L6TOR23WWP3Q7MFY");
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
				fm.cd(tmp);
			} else if (input.startsWith("put")) {
				String tmp = input.substring(4);
				fm.put(tmp, fm.currentFolder);
			} else if (input.startsWith("cat")) {
				String tmp = input.substring(4);
				CSVFile foo = (CSVFile) fm.get(tmp);
				foo.decrypt();
				String bar = new String(foo.getPlainText());
				System.out.println(bar);
			} else if (input.startsWith("get")) {
				String[] tmp = input.substring(4).split(" ");
				String alias = tmp[0];
				String save_path = tmp[1];
				CSVFile foo = (CSVFile) fm.get(alias);
				foo.decrypt();
				CSVFile foobar = foo;
				FileUtils.writeFileToDisk(save_path, foo.getPlainText());
				System.out.println("Wrote file to disk");
			} else if (input.startsWith("mkdir")) {
				String tmp = input.substring("mkdir".length() + 1);
				fm.mkdir(tmp);
			} else if (input.toLowerCase().equals("exit")) {
				System.exit(0);
			} else {
				System.out.println("You SUCK");
			}

		}

	}
}
