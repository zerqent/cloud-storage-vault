package no.ntnu.item.provider;

import java.io.File;
import java.util.List;

public interface CloudProvider {
	
	public List<String>[] getFilesInRootDir();
	
	public List<String>[] getFilesInDirectory(String directory);
	
	public String getAbsolutePath(File file);
	
	public String downloadFile(String absolutePath);
	
	public File downloadFile(File file);
	
	public void uploadFile(File file);
	
	// TODO: Do we need some kind of binary representation?
	public void uploadFile(String filename, String Data);
	
}
