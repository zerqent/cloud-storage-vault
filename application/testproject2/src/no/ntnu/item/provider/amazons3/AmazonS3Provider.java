package no.ntnu.item.provider.amazons3;

import java.util.List;

import no.ntnu.item.provider.CloudProvider;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;

public class AmazonS3Provider implements CloudProvider {

	public AmazonS3Provider() {
		// TODO: Should store these in a separate file, which should not go on github
		String awsAccessKey = "";
		String awsSecretKey = "";
		AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);
		S3Service s3Service;
		try {
			s3Service = new RestS3Service(awsCredentials);
			S3Bucket[] myBuckets = s3Service.listAllBuckets();
			System.out.println("How many buckets to I have in S3? " + myBuckets.length);
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}

		
	}
	
	@Override
	public List[] getAllFiles() {
		// TODO Auto-generated method stub
		return null;
	}

}
