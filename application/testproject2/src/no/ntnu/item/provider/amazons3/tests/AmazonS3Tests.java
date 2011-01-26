package no.ntnu.item.provider.amazons3.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import no.ntnu.item.provider.amazons3.AmazonS3Provider;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AmazonS3Tests {

	private AmazonS3Provider prov;
	private S3Service s3;
	private String testbucket = "unittestbucket";

	@Before
	public void setUp() throws Exception {
		this.prov = new AmazonS3Provider();
		this.s3 = this.prov.getS3Service();
		this.prov.setCurrentBucket(this.s3.getBucket(this.testbucket));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testS3Connection() throws S3ServiceException {
		S3Bucket[] buckets = s3.listAllBuckets();
		Assert.assertThat(buckets.length, is(not(0)));
	}

	@Test
	public void testUpload() throws S3ServiceException {
		S3Object object = new S3Object("object");
		object = s3.putObject(this.prov.getCurrentBucket(), object);
		s3.deleteObject(this.prov.getCurrentBucket(), "object");
	}

	@Test
	public void testUploadInFolder() throws S3ServiceException {
		S3Object object = new S3Object("objectdir/object");
		object = s3.putObject(this.prov.getCurrentBucket(), object);
		s3.deleteObject(this.prov.getCurrentBucket(), "objectdir/object");
	}

	@Test
	public void testUploadInFolderWithRootSlash() throws S3ServiceException {
		// This works but will actually upload to //objectdir/object
		S3Object object = new S3Object("/objectdir/object");
		object = s3.putObject(this.prov.getCurrentBucket(), object);
		s3.deleteObject(this.prov.getCurrentBucket(), "/objectdir/object");
	}

	@Test
	public void testCreateFolder() throws S3ServiceException {
		S3Object object = new S3Object("objectdir/");
		object = s3.putObject(this.prov.getCurrentBucket(), object);
		s3.deleteObject(this.prov.getCurrentBucket(), "objectdir/");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getDirectoryObject() throws S3ServiceException {
		s3.getObject(this.prov.getCurrentBucket(), "static/");
	}

	@SuppressWarnings("deprecation")
	@Test(expected=S3ServiceException.class)
	public void getDirectoryObjectWithOutTrailingSlashCausesException() throws S3ServiceException {
		// This is the wrong way
		s3.getObject(this.prov.getCurrentBucket(), "static");
	}

	@SuppressWarnings("deprecation")
	@Test(expected=S3ServiceException.class)
	public void getDirectoryObjectWithPrefixSlashCausesException() throws S3ServiceException {
		// This is the wrong way
		s3.getObject(this.prov.getCurrentBucket(), "/static/");
	}
}
