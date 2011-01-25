package no.ntnu.item.provider.amazons3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.file.FileContainer;
import no.ntnu.item.provider.CloudProvider;

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
	
	public AmazonS3Provider() throws CloudServiceException {
		// TODO: Should store these in a separate file, which should not go on github
		String awsAccessKey = "AKIAIN6BVRXCIMPQ3DDA";
		String awsSecretKey = "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";
		this.awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
		try {
			this.s3Service = new RestS3Service(awsCredentials);
			this.currentBucket = s3Service.getBucket(this.defaultBucket);
		} catch (S3ServiceException e) {
			throw new CloudServiceException("Unable to connect to Amazon S3");
		}
		
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
		try {
			this.s3Service.getObjectDetails(this.currentBucket, filename);
			return true;
		} catch (S3ServiceException e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public FileContainer downloadFile(String absolutePath) {
		FileContainer container;
		try {
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
			if (placement == null || placement.equals("") || placement.equals("/")) {
				placement = "";
			} else {
				if (!placement.endsWith("/") && placement.length() > 1) {
					placement += "/";
				}	
			}
			
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
		this.s3Service.deleteObject(this.currentBucket, path);
	}
}
