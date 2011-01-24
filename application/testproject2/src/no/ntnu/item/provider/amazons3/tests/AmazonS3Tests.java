package no.ntnu.item.provider.amazons3.tests;


import no.ntnu.item.provider.amazons3.AmazonS3Provider;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.hamcrest.CoreMatchers.*;

@Category(AmazonS3Provider.class)
public class AmazonS3Tests {
	
	private String testbucket = "unittestbucket";
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testS3Connection() throws S3ServiceException {
		AmazonS3Provider prov = new AmazonS3Provider();
		S3Service s3 = prov.getS3Service();
		S3Bucket[] buckets = s3.listAllBuckets();
		Assert.assertThat(buckets.length, is(not(0)));
	}
	
	@Test
	public void testUpload() throws S3ServiceException {
		AmazonS3Provider prov = new AmazonS3Provider();
		S3Bucket bucket = prov.getS3Service().getBucket(this.testbucket);
		prov.setCurrentBucket(bucket);
		
		String filename = "foobar.txt";
		String data = "hello world";
		
		prov.uploadFile(filename, data);
		
		Assert.assertTrue(prov.fileExists(filename));
		
		String downloadedData = prov.downloadFile(filename);
		Assert.assertEquals(data, downloadedData);
		
		
	}
	
	public void testDownload() {
		
	}
	
	public void testThatFilesisCorrect() {
		
	}
	
}
