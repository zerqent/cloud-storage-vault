package no.ntnu.item.provider;

import java.io.File;
import java.util.List;

import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.exception.DirDoesNotExistException;
import no.ntnu.item.exception.FileNotFoundException;
import no.ntnu.item.file.FileContainer;

public interface CloudFileManager {
	
	public String getCwd() throws CloudServiceException;
	
	public void chDir(String path) throws DirDoesNotExistException, CloudServiceException;
	
	public List<String> ls() throws CloudServiceException;
	
	public boolean isFile(String path) throws CloudServiceException;
	
	public boolean isDirectory(String path) throws CloudServiceException;
	
	public FileContainer download(String path) throws FileNotFoundException, CloudServiceException;
	
	public void upload(File file, String destination) throws FileNotFoundException, CloudServiceException; // TODO: Better Exception for this
	
	public void delete(String path) throws FileNotFoundException, CloudServiceException;
}
