package no.ntnu.item.provider.amazons3;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import no.ntnu.item.file.FileContainer;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class AmazonS3Provider{
	
	private AWSCredentials awsCredentials;
	private S3Service s3Service;
	private String defaultBucket = "tileivind"; // TODO: should user care about buckets?
	private S3Bucket currentBucket;
	
	public AmazonS3Provider() throws S3ServiceException {
		Properties configFile = new Properties();
		try {
			configFile.load(this.getClass().getClassLoader().getResourceAsStream("resources/secret.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String awsAccessKey = configFile.getProperty("AWS_KEYID");
		String awsSecretKey = configFile.getProperty("AWS_KEYAC");
		this.awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
		this.s3Service = new RestS3Service(awsCredentials);
		this.currentBucket = s3Service.getBucket(this.defaultBucket);	
	}

	public String getDefaultBucket() {
		return defaultBucket;
	}

	public void setCurrentBucket(S3Bucket currentBucket) {
		this.currentBucket = currentBucket;
	}
	
	public S3Bucket getCurrentBucket() {
		return this.currentBucket;
	}
	
	public S3Service getS3Service() {
		return s3Service;
	}
	
	@SuppressWarnings("deprecation")
	public boolean fileExists(String filename) {
		filename = fixPath(filename, false);
		try {
			this.s3Service.getObjectDetails(this.currentBucket, filename);
			return true;
		} catch (S3ServiceException e) {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean dirExists(String dirname) {
		dirname = fixPath(dirname, true);
		try {
			this.s3Service.getObjectDetails(this.currentBucket, dirname);
			return true;
		} catch (S3ServiceException e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public FileContainer downloadFile(String absolutePath) {
		FileContainer container;
		try {
			absolutePath = fixPath(absolutePath, false);
			S3Object object = this.s3Service.getObject(this.currentBucket, absolutePath);
			container = new FileContainer(object.getContentType());
			container.setEncoding(object.getContentEncoding());
			container.readDataBinary(object.getDataInputStream(), (int)object.getContentLength());
			return container;
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Should not happen
			e.printStackTrace();
		}
		return null;
	}

	public void uploadFile(File file, String placement) {
		try {
			S3Object object = new S3Object(file);
			placement = fixPath(placement, true);
			
			object.setKey(placement + file.getName());
			this.s3Service.putObject(this.currentBucket, object);
		} catch (NoSuchAlgorithmException e) {
			// ON object creation
			e.printStackTrace();
		} catch (IOException e) {
			// On object creation
			e.printStackTrace();
		} catch (S3ServiceException e) {
			// ON Upload
			e.printStackTrace();
		}
	}
	
	public void deleteFile(String path) throws S3ServiceException {
		path = fixPath(path, false);
		this.s3Service.deleteObject(this.currentBucket, path);
	}
	
	
	/**
	 * Utility function to convert paths to a form which Jets3t/Amazon S3 will accept.
	 * @param path
	 * @param isFolder
	 * @return A path that should actually work
	 */
	private static String fixPath(String path, boolean isFolder) {
		if (path == null || path.equals("/") || path.trim().equals("")) {
			return "";
		}
		
		if (isFolder && !path.endsWith("/") && path.length()>1) {
			path += "/";
		}
		
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}
	
}
