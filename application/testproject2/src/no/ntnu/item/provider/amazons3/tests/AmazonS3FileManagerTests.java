package no.ntnu.item.provider.amazons3.tests;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import no.ntnu.item.Configuration;
import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.exception.DirDoesNotExistException;
import no.ntnu.item.provider.CloudFileManager;
import no.ntnu.item.provider.amazons3.AmazonS3FileManager;
import no.ntnu.item.provider.amazons3.AmazonS3Provider;

import org.jets3t.service.S3ServiceException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AmazonS3FileManagerTests {
	
	private AmazonS3Provider provider;
	private String testbucket = "unittestbucket";
	private CloudFileManager fm;
	
	@Before
	public void setUp() throws Exception {
		this.provider = new AmazonS3Provider();
		this.provider.setCurrentBucket(this.provider.getS3Service().getBucket(this.testbucket));
		this.fm = new AmazonS3FileManager(this.provider);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCwd() throws DirDoesNotExistException, CloudServiceException {
		Assert.assertEquals("/",this.fm.getCwd());
		this.fm.chDir("static");
		Assert.assertEquals("/static/",this.fm.getCwd());
		this.fm.chDir("subdir1");
		Assert.assertEquals("/static/subdir1/",this.fm.getCwd());
		this.fm.chDir("/");
		Assert.assertEquals("/",this.fm.getCwd());
		this.fm.chDir("/static/subdir1/");
		Assert.assertEquals("/static/subdir1/",this.fm.getCwd());
	}
		
	@Test
	public void testChDirRelative() throws DirDoesNotExistException, CloudServiceException {
		this.fm.chDir("static");
		Assert.assertEquals("/static/",this.fm.getCwd());
		List<String> tmp = this.fm.ls();
		Assert.assertTrue(tmp.contains("subdir1"));
	}
	
	@Test
	public void testChDirAbsolute() throws DirDoesNotExistException, CloudServiceException, S3ServiceException {
		this.fm.chDir("/static/subdir1/");
		Assert.assertEquals("/static/subdir1/",this.fm.getCwd());
		List<String> tmp = this.fm.ls();
		Assert.assertTrue(tmp.contains("testfile1.txt"));
	}
	
	@Test
	public void testParentFolderChDir() throws DirDoesNotExistException, CloudServiceException {
		this.fm.chDir("static");
		this.fm.chDir("..");
		Assert.assertEquals("/",this.fm.getCwd());
	}
	
	@Test
	public void testSamefolderChDir() throws DirDoesNotExistException, CloudServiceException {
		this.fm.chDir("static");
		String org = this.fm.getCwd();
		this.fm.chDir(".");
		Assert.assertEquals(org, this.fm.getCwd());
	}
	
	@Test(expected=DirDoesNotExistException.class)
	public void testChDirIntoFile() throws DirDoesNotExistException, CloudServiceException {
		this.fm.chDir("/static/subdir1/testfile1.txt");	
	}
	
	@Test
	public void testLs() throws DirDoesNotExistException, CloudServiceException {
		this.fm.chDir("static");
		List<String> tmp = this.fm.ls();
		Assert.assertTrue(tmp.contains("subdir1"));
		Assert.assertTrue(tmp.contains("subdir2"));
		Assert.assertTrue(tmp.contains("staticfile.txt"));
	}
	
	@Test
	public void testThatLsDoesNotReturnFilesInSubFolders() throws DirDoesNotExistException, CloudServiceException {
		this.fm.chDir("static");
		List<String> tmp = this.fm.ls();
		Assert.assertFalse(tmp.contains("subdir1/testfile1.txt"));
	}
	
	@Test
	public void testIsFile() throws CloudServiceException, DirDoesNotExistException {
		Assert.assertThat(this.fm.isFile("static"), is(false));
		this.fm.chDir("static");
		Assert.assertThat(this.fm.isFile("staticfile.txt"), is(true));
		Assert.assertThat(this.fm.isFile("subdir1"), is(false));
		this.fm.chDir("subdir1");
		Assert.assertThat(this.fm.isFile("testfile1.txt"), is(true));
	}
	
	@Test
	public void testIsFolder() throws CloudServiceException, DirDoesNotExistException {
		Assert.assertThat(this.fm.isDirectory("static"), is(true));
		this.fm.chDir("static");
		Assert.assertThat(this.fm.isDirectory("staticfile.txt"), is(false));
		Assert.assertThat(this.fm.isDirectory("subdir1"), is(true));
		this.fm.chDir("subdir1");
		Assert.assertThat(this.fm.isDirectory("testfile1.txt"), is(false));
	}
	
}
