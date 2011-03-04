package no.ntnu.item.csv.csvobject.man;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.csvobject.impl.CSVFileFacade;
import no.ntnu.item.csv.csvobject.impl.CSVFolderImpl;

import org.apache.http.client.ClientProtocolException;

public class CSVFileManager {

	private Stack<Capability> location;
	private CSVFolderImpl currentFolder;
	
	public CSVFileManager(CSVFolderImpl root){
		this.location = new Stack<Capability>();
		this.location.add(root.getCapability());
		this.currentFolder = root;
	}
	
	public CSVFolderImpl getCurrentFolder(){
		return this.currentFolder;
	}
	
	public void put(String filepath) throws IOException{
		if(this.currentFolder.getCapability().getType() != CapabilityType.RW){
			System.out.println("ERROR: You do not have permission to write to this directory.");
			return;
		}
			
		File content = new File(filepath);
		if(this.currentFolder.getContents().containsKey(content.getName())){
			System.out.println("ERROR: A file with the same name already exists. Please rename your file before uploading it.");
			return;
		}

		CSVObject file = null;
		if(content.isDirectory()){
			// TODO Initialize FolderFacade
			file = new CSVFolderImpl();
		}else if(content.isFile()){
			CSVFileFacade fac = new CSVFileFacade();
			fac.setPlainText(content);
			file = fac;
		}else{
			System.out.println("ERROR: Can not upload a non-existing file.");
			return;
		}
		file.encrypt();
		
		switch (Communication.put(file, Communication.SERVER_PUT)){
			case 200: System.out.println("File successfully uploaded.");break;
			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty object.");return;
			default: System.out.println("ERROR: An unexpected error occured.");return;
		}
		
		
		this.currentFolder.getContents().put(content.getName(), file.getCapability());
		this.currentFolder.encrypt();
		
		switch (Communication.put(this.currentFolder, Communication.SERVER_PUT)){
			case 200: System.out.println("Parent Folder successfully changed.");break;
			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty folder.");return;
			default: System.out.println("ERROR: An unexpected error occured.");return;
		}
		
	}

	public CSVObject get(String alias) throws ClientProtocolException, IOException{
		Capability cap;
		if((cap = this.currentFolder.getContents().get(alias)) == null){
			System.out.println("ERROR: File " + alias + " does not exist.");
			return null;
		}
		byte[] response = Communication.get(cap.getStorageIndex(), Communication.SERVER_GET);
		// TODO: Find out how to obtain the received object!
		
		return null;
	}
	
	public void ls(){
		Map<String, Capability> content = this.currentFolder.getContents();
	}
	
	public void cd(String folderAlias){
		
	}
	
	public static void main(String[] args){
		
	}
}
