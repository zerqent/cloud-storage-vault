package no.ntnu.item.provider.amazons3;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.exception.DirDoesNotExistException;
import no.ntnu.item.exception.FileNotFoundException;
import no.ntnu.item.provider.CloudFileManager;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Object;

public class AmazonS3FileManager implements CloudFileManager {
	
	private Stack<String> location;
	private final String defaultStart = "/";
	private AmazonS3Provider provider;
	
	public AmazonS3FileManager(AmazonS3Provider provider) {
		this.provider = provider;
		this.location = new Stack<String>();
		//this.location.push(this.defaultStart);
	}
	
	public String getCwd() {
		String tmp = "/";
		for (Iterator<String> iterator = this.location.iterator(); iterator.hasNext();) {
			String folder = iterator.next();
			tmp += folder + "/";
		}
		return tmp;
	}
	
	public void chDir(String dir) throws DirDoesNotExistException, CloudServiceException {
		String tmp = this.getAbsPath(dir);
//		if (!this.isDirectory(dir)) {
//			System.out.println("Not directory");
//			throw new DirDoesNotExistException(null);
//		}
		
		if (dir.startsWith("/")) {
			this.location = new Stack<String>();
		}
		String[] tmp2 = dir.split("/");
		for (String folder : tmp2) {
			if (folder != null && !folder.equals("")) {
				this.location.push(folder);
			}
		}
	}
	
	public List<String> ls() throws CloudServiceException{
		String cwd = this.getCwd();
		cwd = cwd.substring(1);
		S3Object[] object;
		try {
			object = this.provider.getS3Service().listObjects(this.provider.getCurrentBucket(),cwd,null);		
			List<String> files = new ArrayList<String>();
			
			for (S3Object s3Object : object) {
				String tmp = s3Object.getKey();
				tmp = tmp.substring(cwd.length());
				if (tmp != null && !tmp.trim().equals("")) {
					// Filter files/folders in subdirs
					String[] foo = tmp.split("/");
					if (foo.length==1) {
						files.add(foo[0].trim());
					}
				}
			}
			Collections.sort(files);
			return files;
		} catch (S3ServiceException e) {
			throw new CloudServiceException(null);
		}
	}

	private String getAbsPath(String path) {
		if (path.startsWith("/")) {
			return path;
		}
		return this.getCwd() + path;
	}
	
	public boolean isFile(String path) throws CloudServiceException {
		String abspath = this.getAbsPath(path);
		if(path.endsWith("/") || abspath.endsWith("/")) {
			return false;
		}
		try {
			boolean onServer = this.provider.fileExists(abspath);
			return onServer;
			// TODO: Need to actually check on server;
		} catch (S3ServiceException e) {
			throw new CloudServiceException(null);
		}
	}

	public boolean isDirectory(String path) throws CloudServiceException {
		String abspath = this.getAbsPath(path) + "/";
//		if (!path.endsWith("/") && !abspath.endsWith("/")) {
//			return false;
//		}
		try {
			boolean onServer = this.provider.fileExists(abspath);
			return onServer;
		} catch (S3ServiceException e) {
			throw new CloudServiceException(null);
		}
	}

	public File download(String path) throws FileNotFoundException,
			CloudServiceException {
		String abspath = this.getAbsPath(path);
		String a = this.provider.downloadFile(abspath);
		// TODO: Need to look at how binary data/not text will work
		return null;
	}

	public void upload(File file, String destination) throws FileNotFoundException,
			CloudServiceException {
		String abspath = this.getAbsPath(destination);
		this.provider.uploadFile(file);
	}

	
}
