package no.ntnu.item.provider.amazons3.tests;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.io.IOException;

import no.ntnu.item.file.FileContainer;
import no.ntnu.item.provider.amazons3.AmazonS3Provider;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(AmazonS3Provider.class)
public class AmazonS3Tests {
	
	private String testbucket = "unittestbucket";
	private AmazonS3Provider prov;
	private S3Service s3;
	
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
		File file = new File("testing/test1.txt");
		prov.uploadFile(file, "/");
		Assert.assertTrue(prov.fileExists("test1.txt"));
		
		FileContainer foo = prov.downloadFile("test1.txt");
		Assert.assertEquals("8410410511532105115321161011151164946116120116", foo.toString());
		
		this.prov.deleteFile("test1.txt");
		Assert.assertFalse(prov.fileExists("test1.txt"));
	}
	
	@Test
	public void testFileExists() {
		Assert.assertThat(prov.fileExists("static/"), is(true));
		Assert.assertThat(prov.fileExists("static/subdir1/testfile1.txt"), is(true));
	}
	
	@Test
	public void testDownload() {
		FileContainer foo = prov.downloadFile("static/staticfile.txt");
		Assert.assertEquals("fdaf3f8024ea341bb195c275cf2f378a2dabfb9c", foo.hexDigest());
	}
	
	@Test
	public void testThatFilesisCorrect() throws IOException {
		File file = new File("testing/test1.txt");
		FileContainer foo = FileContainer.fromFile(file);
		String md1 = foo.hexDigest();
		
		prov.uploadFile(file, null);
		FileContainer foo2 = prov.downloadFile("test1.txt");
		String md2 = foo2.hexDigest();
		Assert.assertEquals(md1, md2);
	}
	
}
