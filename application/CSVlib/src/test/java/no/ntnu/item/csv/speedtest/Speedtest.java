package no.ntnu.item.csv.speedtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.communication.CommunicationFactory;
import no.ntnu.item.csv.contrib.com.bitzi.util.Base32;
import no.ntnu.item.csv.csvobject.CSVFile;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.filemanager.CSVFileManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Speedtest {
	private Communication com = CommunicationFactory
			.createCommunicationFromProperties();

	private CSVFileManager fm = new CSVFileManager(this.com);

	@Ignore
	@Before
	public void setUp() throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException {
		CSVFolder root = new CSVFolder();
		this.fm.uploadFolder(root);
		this.fm.setRootCapability(root.getCapability());

	}

	@Ignore
	@Test
	public void test100FolderCreation() throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException {
		CSVFolder folder;
		long before = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			folder = new CSVFolder();
			this.fm.uploadFolder(folder);
		}
		long after = System.currentTimeMillis();
		long diff = after - before;
		System.out.println("100 folders total:" + diff);
		System.out.println("100 folders avg:" + diff / 100);
	}

	@Ignore
	@Test
	public void test100MBuploadEncrypted() throws FileNotFoundException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {
		File f = new File("/tmp/sizes/100mb");
		uploadnFiles(f, 100, 5);
	}

	@Ignore
	@Test
	public void test1000MBUpDownEncrypted() throws FileNotFoundException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, RemoteFileDoesNotExistException,
			FailedToVerifySignatureException {
		// Need to monitor this in an external application to get the speed
		File f = new File("/home/eiriha/output.dat");
		CSVFile file = new CSVFile(f);
		this.fm.uploadFile(file);

		Capability cap = file.getCapability();
		CSVFile down = new CSVFile(cap, new File("/home/eiriha/foobar.tmp"));
		this.fm.downloadFile(down);
	}

	@Ignore
	@Test
	public void testUploadSpeedNotEncrypted()
			throws ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException, IOException {
		// Need to monitor this in a separate application

		File f = new File("/tmp/sizes/100mb");
		FileInputStream fis = new FileInputStream(f);
		String url = getARandomUrl();
		this.com.putInputStream(url, fis, f.length());

		HttpResponse response = this.com.get(url);
		HttpEntity entity = response.getEntity();

		OutputStream os = new FileOutputStream("/tmp/sizes/100mb_down");
		entity.writeTo(os);
		os.flush();
		os.close();
	}

	@Ignore
	@Test
	public void test1MBupload100Times() throws FileNotFoundException,
			ServerCommunicationException, InvalidWriteEnablerException,
			ImmutableFileExistsException {
		File f = new File("/tmp/sizes/1mb");
		uploadnFiles(f, 1, 100);
	}

	@Ignore
	@Test
	public void test1MBuploadUnencrypted100Times()
			throws FileNotFoundException, ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException {
		File f = new File("/tmp/sizes/1mb");
		uploadnUnencryptedFiles(f, 1, 100);
	}

	private void uploadnFiles(File f, int sizeMB, int count)
			throws FileNotFoundException, ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException {
		CSVFile file;
		long before = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			file = new CSVFile(f);
			this.fm.uploadFile(file);
		}
		long after = System.currentTimeMillis();
		long diff = after - before;
		System.out.println("ENCRYPTED Upload " + count + " files of " + sizeMB
				+ "MB total:" + diff);
		System.out.println("ENCRYPTED Upload " + count + " files of " + sizeMB
				+ "MB avg:" + diff / count);

		double best = (sizeMB * count) / 12.5;
		double actual = diff / 1000;
		double percent = best / actual * 100;
		System.out.println("ENCRYPTED Upload " + count + "MB files of "
				+ sizeMB + " bandwidth:" + percent);

	}

	// Upload UNENCRYPTED FILES!
	private void uploadnUnencryptedFiles(File f, int sizeMB, int count)
			throws FileNotFoundException, ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException {
		FileInputStream fis;

		String[] urls = new String[count];
		for (int i = 0; i < urls.length; i++) {
			urls[i] = getARandomUrl();
		}

		long before = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			// file = new CSVFile(f);
			// this.fm.uploadFile(file);
			fis = new FileInputStream(f);
			this.com.putInputStream(urls[i], fis, f.length());
		}
		long after = System.currentTimeMillis();
		long diff = after - before;
		System.out.println("UNENCRYPTED Upload " + count + " files of "
				+ sizeMB + "MB total:" + diff);
		System.out.println("UNENCRYPTED Upload " + count + " files of "
				+ sizeMB + "MB avg:" + diff / count);

		double best = (sizeMB * count) / 12.5;
		double actual = diff / 1000;
		double percent = best / actual * 100;
		System.out.println("UNENCRYPTED Upload " + count + " files of "
				+ sizeMB + "MB bandwidth:" + percent);

	}

	private String getARandomUrl() {
		String foo = "" + Math.random() * 100000;
		byte[] tmp = Cryptoutil.hash(foo.getBytes(), 16);
		return Base32.encode(tmp);
	}

}
