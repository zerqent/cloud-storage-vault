package no.ntnu.item.csv.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.DuplicateAliasException;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.IllegalFileNameException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InsufficientPermissionException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.NoSuchAliasException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.fileutils.FileUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSVFileManagerTest {

	private CSVFileManager fileManager;
	private final String testfile = "src/test/resources/smallloremipsum.txt";
	private final String decryptedOutputFile = "/tmp/decoutput.txt";
	private final Communication connection = new Communication(
			"create.q2s.ntnu.no", "eiriha", "komle123");

	private CSVFile csvFile;
	private CSVFolder folder;

	@Before
	public void setUp() throws IOException, ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException {

		Assert.assertFalse("You must set the user/password", this.connection
				.getPassword().equals("")
				|| this.connection.getUsername().equals(""));

		this.fileManager = new CSVFileManager(connection);
		CSVFolder folder = new CSVFolder();
		this.fileManager.uploadFolder(folder);
		this.fileManager.setRootCapability(folder.getCapability());

	}

	@Test
	public void checkShareFolder() {
		Assert.assertNotNull(this.fileManager.getShareFolder());
		Assert.assertTrue(this.fileManager.getShareFolder().isValid());
	}

	@Test
	public void testFileUpload() throws FileNotFoundException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {
		File file = new File(this.testfile);
		csvFile = new CSVFile(file);
		csvFile = this.fileManager.uploadFile(csvFile);
		Assert.assertTrue(csvFile.isValid());

	}

	@Test
	public void testFileDownload() throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			RemoteFileDoesNotExistException, IOException,
			FailedToVerifySignatureException {
		testFileUpload();
		File myFile = new File(this.decryptedOutputFile);
		CSVFile download = new CSVFile(csvFile.getCapability(), myFile);
		download = this.fileManager.downloadFile(download);

		Assert.assertTrue(download.isValid());

		Assert.assertTrue(myFile.exists());

		File testFile = new File(this.testfile);

		byte[] org = FileUtils.readDataBinary(new FileInputStream(testFile),
				(int) testFile.length());
		byte[] downloaded = FileUtils.readDataBinary(
				new FileInputStream(myFile), (int) myFile.length());

		Assert.assertArrayEquals(org, downloaded);

	}

	@Test
	public void testFolderUpload() throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			FileNotFoundException {
		this.folder = new CSVFolder();
		this.csvFile = new CSVFile(new File(this.testfile));
		this.folder.addContent("hello", this.csvFile.getCapability());
		this.fileManager.uploadFolder(folder);
	}

	@Test
	public void testFolderDownload() throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			FileNotFoundException, RemoteFileDoesNotExistException,
			FailedToVerifySignatureException {
		testFolderUpload();

		CSVFolder newFolder = new CSVFolder(this.folder.getCapability());
		newFolder = this.fileManager.downloadFolder(newFolder);
		Assert.assertTrue(newFolder.isValid());
		Assert.assertTrue(newFolder.getContents().containsKey("hello"));

	}

	@Test
	public void testUploadFileIntoCurrentfolder()
			throws IllegalFileNameException, DuplicateAliasException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException,
			IOException, InsufficientPermissionException,
			FailedToVerifySignatureException {
		this.csvFile = new CSVFile(new File(this.testfile));
		this.fileManager.putObjectIntoCurrentFolder(this.csvFile, "myFile");

		Assert.assertTrue(this.fileManager.getCurrentFolder().getContents()
				.containsKey("myFile"));

		CSVFile file = new CSVFile(this.csvFile.getCapability(), new File(
				this.decryptedOutputFile));
		file = this.fileManager.downloadFile(file);

		File myFile = new File(this.decryptedOutputFile);
		File testFile = new File(this.testfile);

		byte[] org = FileUtils.readDataBinary(new FileInputStream(testFile),
				(int) testFile.length());
		byte[] downloaded = FileUtils.readDataBinary(
				new FileInputStream(myFile), (int) myFile.length());

		Assert.assertArrayEquals(org, downloaded);
	}

	@Test
	public void testUploadFolderIntoFolder() throws FileNotFoundException,
			IllegalFileNameException, DuplicateAliasException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException,
			InsufficientPermissionException, FailedToVerifySignatureException {
		CSVFolder folder = new CSVFolder();
		this.csvFile = new CSVFile(new File(this.testfile));
		folder.addContent("myFile", this.csvFile.getCapability());

		this.fileManager.putObjectIntoCurrentFolder(folder, "myFolder");

		CSVFolder downFolder = this.fileManager.downloadFolder("myFolder",
				this.fileManager.getCurrentFolder());
		Assert.assertTrue(downFolder.isValid());
		Assert.assertTrue(downFolder.getContents().containsKey("myFile"));
	}

	@Test
	public void testCDtoValidFolder() throws IllegalFileNameException,
			DuplicateAliasException, ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			RemoteFileDoesNotExistException, NoSuchAliasException,
			InsufficientPermissionException, FailedToVerifySignatureException {
		CSVFolder folder = new CSVFolder();
		this.fileManager.putObjectIntoCurrentFolder(folder, "myFolder");

		folder = this.fileManager.cd("myFolder");
		Assert.assertTrue(folder.isValid());
	}

	@Test(expected = NoSuchAliasException.class)
	public void testCDToInvalidFolder() throws ServerCommunicationException,
			RemoteFileDoesNotExistException, NoSuchAliasException,
			FailedToVerifySignatureException {
		folder = this.fileManager.cd("myFolder");
	}

	@Test(expected = RemoteFileDoesNotExistException.class)
	public void testGetInvalidFile() throws FileNotFoundException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException,
			FailedToVerifySignatureException {
		this.csvFile = new CSVFile(new File(this.testfile));
		this.fileManager.downloadFile(csvFile);
	}

	@Test
	public void testGetParentFolder() throws FileNotFoundException,
			IllegalFileNameException, DuplicateAliasException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, InsufficientPermissionException,
			RemoteFileDoesNotExistException, NoSuchAliasException,
			FailedToVerifySignatureException {
		CSVFolder folder = new CSVFolder();

		this.fileManager.putObjectIntoCurrentFolder(folder, "omg");

		this.fileManager.cd("omg");

		folder = this.fileManager.getParentFolder();

		Assert.assertTrue(folder.getContents().containsKey("omg"));
	}

	@Test
	public void testGetAliasOfCurrentFolder() throws IllegalFileNameException,
			DuplicateAliasException, ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			InsufficientPermissionException, RemoteFileDoesNotExistException,
			NoSuchAliasException, FailedToVerifySignatureException {
		CSVFolder folder = new CSVFolder();
		this.fileManager.putObjectIntoCurrentFolder(folder, "alias");
		this.fileManager.cd("alias");
		Assert.assertEquals("alias", this.fileManager.getAliasOfCurrentFolder());
	}

	@Test
	public void testReplaceCurrentFolder() throws FileNotFoundException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, DuplicateAliasException,
			IllegalFileNameException, InsufficientPermissionException,
			RemoteFileDoesNotExistException, NoSuchAliasException,
			FailedToVerifySignatureException {
		CSVFolder old = new CSVFolder();
		this.fileManager.putObjectIntoCurrentFolder(old, "alias");
		CSVFile file = new CSVFile(new File(this.testfile));
		this.fileManager.putObjectIntoFolder(file, old, "afile");

		Assert.assertTrue(this.fileManager.getCurrentFolder().getContents()
				.containsKey("alias"));
		this.fileManager.cd("alias");
		Assert.assertTrue(this.fileManager.getCurrentFolder().getContents()
				.containsKey("afile"));

		CSVFolder folder = this.fileManager.replaceCurrentFolder();
		Assert.assertArrayEquals(folder.getPublicKeyHash(), this.fileManager
				.getCurrentFolder().getPublicKeyHash());
		Assert.assertEquals("alias", this.fileManager.getAliasOfCurrentFolder());
		Assert.assertTrue(this.fileManager.getCurrentFolder().getContents()
				.containsKey("afile"));

		this.fileManager.cd("..");
		Assert.assertTrue(this.fileManager.getCurrentFolder().getContents()
				.containsKey("alias"));
		this.fileManager.cd("alias");
		Assert.assertTrue(this.fileManager.getCurrentFolder().getContents()
				.containsKey("afile"));

		Assert.assertFalse(this.fileManager.getCurrentFolder().getCapability()
				.toString().equals(old.getCapability().toString()));

		// Check that old object has been emptied
		CSVFolder newCopyOfOld = this.fileManager.downloadFolder(old
				.getCapability());
		Assert.assertTrue(newCopyOfOld.getContents().size() == 0);

	}

	@Test
	public void testReplaceRootFolder() throws ServerCommunicationException,
			InvalidWriteEnablerException, InsufficientPermissionException,
			FileNotFoundException, ImmutableFileExistsException,
			DuplicateAliasException, IllegalFileNameException {
		CSVFolder oldRoot = this.fileManager.getRootFolder();
		CSVFile file = new CSVFile(new File(this.testfile));
		this.fileManager.putObjectIntoFolder(file, oldRoot, "afile");

		CSVFolder newRoot = this.fileManager.replaceCurrentFolder();
		Assert.assertFalse(oldRoot
				.getCapability()
				.toString()
				.equals(this.fileManager.getRootFolder().getCapability()
						.toString()));
		Assert.assertTrue(this.fileManager.getRootFolder().getContents()
				.containsKey("afile"));
		Assert.assertFalse(this.fileManager.getRootFolder() == oldRoot);
		Assert.assertTrue(newRoot == this.fileManager.getRootFolder());
	}

}
