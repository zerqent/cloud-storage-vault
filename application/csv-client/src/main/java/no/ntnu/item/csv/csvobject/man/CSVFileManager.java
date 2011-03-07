package no.ntnu.item.csv.csvobject.man;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.csvobject.impl.CSVFileImpl;
import no.ntnu.item.csv.csvobject.impl.CSVFolderImpl;

import org.apache.http.client.ClientProtocolException;

public class CSVFileManager {

	private Stack<Capability> location;		//Stack storing capabilities of parent directories
	private CSVFolderImpl currentFolder;	//The current folder object visited
	
	public CSVFileManager(CSVFolderImpl root){
		this.location = new Stack<Capability>();
		this.currentFolder = root;
	}
	
	public void put(String filepath, CSVFolderImpl folder) throws IOException{
		//Check write permissions
		if(folder.getCapability().getType() != CapabilityType.RW){
			System.out.println("ERROR: You do not have permission to write to this directory.");
			return;
		}
		
		//Create file content and check for duplicate filenames
		File content = new File(filepath);
		if(folder.getContents().containsKey(content.getName())){
			System.out.println("ERROR: A file with the same name already exists. Please rename your file before uploading it.");
			return;
		}
		
		//Create file or folder object
		CSVObject file = null;
		if(content.isDirectory()){
			file = new CSVFolderImpl();
		}else if(content.isFile()){
			file = new CSVFileImpl(content);
		}else{
			System.out.println("ERROR: Can not upload a non-existing file.");
			return;
		}
		file.encrypt();
		
		//Upload file
		switch (Communication.put(file, Communication.SERVER_PUT)){
			case 200: System.out.println("File successfully uploaded.");break;
			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty object.");return;
			default: System.out.println("ERROR: An unexpected error occured.");return;
		}
		
		//Insert file capability and alias into parent directory and upload parent directory
		folder.getContents().put(content.getName(), file.getCapability());
		folder.encrypt();
		
		switch (Communication.put(folder, Communication.SERVER_PUT)){
			case 200: System.out.println("Parent Folder successfully changed.");break;
			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty folder.");return;
			default: System.out.println("ERROR: An unexpected error occured.");return;
		}
		
		//If content of uploaded File is a folder: put all sub files and sub folders!
		if(content.isDirectory() && file instanceof CSVFolderImpl){
			String[] subfiles = content.list();
			for(int i = 0; i < subfiles.length; i++)
				this.put(content.getAbsolutePath() + "/" + subfiles[i], (CSVFolderImpl)file);
		}
	}

	public CSVObject get(String alias) throws ClientProtocolException, IOException{
		Capability cap;
		if((cap = this.currentFolder.getContents().get(alias)) == null){
			System.out.println("ERROR: File " + alias + " does not exist.");
			return null;
		}
		
		byte[] resp = Communication.get(cap.getStorageIndex(), Communication.SERVER_GET);
		CSVObject file;
		switch(resp[0]){
			case 0: file = CSVFolderImpl.createFromByteArray(resp, cap);break;
			case 1: file = CSVFileImpl.createFromByteArray(resp, cap);break;
			default: file = null;
		}
		return file;
	}
	
	public void ls(){
		Map<String, Capability> content = this.currentFolder.getContents();
		if(content.size() > 0){
			System.out.println("File name \tStorage index \t\t\t\tCapability type");
			for(Map.Entry<String, Capability> entry : content.entrySet())
				System.out.println(entry.getKey() + " " + entry.getValue().getStorageIndex() + " " + entry.getValue().getType());
		}
	}
	
	public void cd(String folderAlias) throws ClientProtocolException, IOException{
		CSVObject folder;
		if(!folderAlias.equals("..")){
			folder = this.get(folderAlias);
			if(folder == null || folder instanceof CSVFile)
				return;
			this.location.push(this.currentFolder.getCapability());
			this.currentFolder = (CSVFolderImpl)folder;
		}else{
			byte[] resp = Communication.get(this.location.lastElement().getStorageIndex(), Communication.SERVER_GET);
			folder = CSVFolderImpl.createFromByteArray(resp, this.location.lastElement());
			if(folder == null || folder instanceof CSVFile)
				return;
			this.currentFolder = (CSVFolderImpl)folder;
			this.location.pop();
		}
	}
	
//	public void mkdir(String alias) throws IOException{
//		CSVFolderImpl folder = new CSVFolderImpl();
//		folder.encrypt();
//		
//		//Upload folder
//		switch (Communication.put(folder, Communication.SERVER_PUT)){
//			case 200: System.out.println("Folder " + alias + " was successfully created.");break;
//			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
//			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
//			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty folder.");return;
//			default: System.out.println("ERROR: An unexpected error occured.");return;
//		}
//		
//	}
	
	public static void main(String[] args){
		
	}
}
