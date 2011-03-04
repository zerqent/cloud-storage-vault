package no.ntnu.item.csv.csvobject.man;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.csvobject.impl.CSVFileFacade;

public class CSVFileManager {

	private Stack<Capability> location;
	private CSVObject current;
	
	public CSVFileManager(CSVObject root){
		this.location = new Stack<Capability>();
		this.location.add(root.getCapability());
		this.current = root;
	}
	
	public void put(String filepath) throws IOException{
		if(this.current.getCapability().getType() != CapabilityType.RW){
			System.out.println("ERROR: You do not have permission to write to this directory.");
			return;
		}
			
		File content = new File(filepath);
		CSVObject file = null;
		if(content.isDirectory()){
			// TODO Initialize FolderFacade
			//file = new CSVFileFacade();
		}else if(content.isFile()){
			CSVFileFacade fac = new CSVFileFacade();
			fac.setPlainText(content);
			file = fac;
		}else{
			System.out.println("ERROR: Can not upload a non-existing file.");
			return;
		}
		file.encrypt();
		
		switch (Communication.put(file, "http://129.241.205.111/put")){
			case 200: System.out.println("File successfully uploaded.");break;
			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty object.");return;
			default: System.out.println("ERROR: An unexpected error occured.");return;
		}
		
		this.current.addContent(content.getName(), file.getCapability());
		
		switch (Communication.put(this.current, "http://129.241.205.111/put")){
			case 200: System.out.println("Parent Folder successfully changed.");break;
			case 400: System.out.println("ERROR: 400 - Bad Request.");return;
			case 500: System.out.println("ERROR: 500 - Internal server error.");return;
			case 600: System.out.println("ERROR: Missing server address, or Trying to upload an empty folder.");return;
			default: System.out.println("ERROR: An unexpected error occured.");return;
		}
	}

	public void get(String alias){
		
	}
	
	public void ls(){
		
	}
	
	public static void main(String[] args){
		
	}
}
