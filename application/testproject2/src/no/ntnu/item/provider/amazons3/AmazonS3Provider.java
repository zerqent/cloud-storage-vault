package no.ntnu.item.provider.amazons3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import no.ntnu.item.provider.CloudProvider;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class AmazonS3Provider implements CloudProvider {
	
	private AWSCredentials awsCredentials;
	private S3Service s3Service;
	private String defaultBucket = "testvault2"; // TODO: should user care about buckets?
	private S3Bucket currentBucket;
	
	public AmazonS3Provider() {
		// TODO: Should store these in a separate file, which should not go on github
		String awsAccessKey = "";
		String awsSecretKey = "";
		this.awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
		try {
			this.s3Service = new RestS3Service(awsCredentials);
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
			this.currentBucket = s3Service.getBucket(this.defaultBucket);
		} catch (S3ServiceException e) {
			// Fail to connect
			e.printStackTrace();
		}
		this.testFolders();
		
	}

	public String getDefaultBucket() {
		return defaultBucket;
	}

	public void setCurrentBucket(S3Bucket currentBucket) {
		this.currentBucket = currentBucket;
	}

	public S3Service getS3Service() {
		return s3Service;
	}
	
	
	public boolean fileExists(String filename) {
		try {
			this.s3Service.getObjectDetails(this.currentBucket, filename);
		} catch (S3ServiceException e) {
			return false;
		}
		return true;
	}

	public List<String>[] getFilesInRootDir() {
		return null;
	}

	public List<String>[] getFilesInDirectory(String directory) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAbsolutePath(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	public String downloadFile(String absolutePath) {
		try {
			S3Object object = this.s3Service.getObject(this.currentBucket, absolutePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(object.getDataInputStream()));
			String data = "";
			String tmp = "";
			while ((data = reader.readLine()) != null) {
				tmp += data;
			}
			return tmp;
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public File downloadFile(File file) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void uploadFile(String filename, String Data) {
		S3Object object;
		try {
			object = new S3Object(filename, Data);
			this.s3Service.putObject(this.currentBucket, object);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void uploadFile(File file) {
		try {
			S3Object object = new S3Object(file);
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
	
	private void testCreation() {
		// TODO: DELETE ME
		// Create an empty object with a key/name, and print the object's details.
		S3Object object = new S3Object("object");
		System.out.println("S3Object before upload: " + object);

		// Upload the object to our test bucket in S3.
		try {
			object = s3Service.putObject(this.currentBucket, object);
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Print the details about the uploaded object, which contains more information.
		System.out.println("S3Object after upload: " + object);
		
	}
	
	public void testFolders() {
		// TODO: DELETE ME
		String foo = "Hello world";
		String name = "foobar.txt";
		try {
			S3Object object = new S3Object(name, foo);
			this.s3Service.putObject(this.currentBucket,object);
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



}
