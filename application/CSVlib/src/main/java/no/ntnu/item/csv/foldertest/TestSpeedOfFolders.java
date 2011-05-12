package no.ntnu.item.csv.foldertest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.communication.Communication;
import no.ntnu.item.csv.communication.CommunicationFactory;
import no.ntnu.item.csv.contrib.com.bitzi.util.Base32;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.exception.FailedToVerifySignatureException;
import no.ntnu.item.csv.exception.ImmutableFileExistsException;
import no.ntnu.item.csv.exception.InvalidWriteEnablerException;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;
import no.ntnu.item.csv.filemanager.CSVFileManager;

public class TestSpeedOfFolders {

	private CSVFileManager manager;
	private int[] points = { 1, 5, 10, 50, 100, 250, 500, 750, 1000, 2500,
			5000, 7500 };

	private String log = new String();
	// private HashMap<String, Capability> testData = new HashMap<String,
	// Capability>();
	private String[] aliases = new String[10000];
	private Capability[] caps = new Capability[10000];

	// private int[] points = { 10000 };

	public TestSpeedOfFolders(CSVFileManager manager) {
		this.manager = manager;
	}

	private void readTestData() throws IOException {
		InputStream is = getClass().getResourceAsStream("/data.txt");
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		String tmp[];
		int i = 0;
		while ((strLine = br.readLine()) != null
				&& i < points[points.length - 1]) {
			tmp = strLine.split(";");
			caps[i] = CapabilityImpl.fromString(tmp[1]);
			aliases[i] = tmp[0];
			i++;
		}

	}

	public void doIt() {

		try {
			readTestData();
			this.testSizeOfFakeAlias();
			testBlankFolderCreation();
			testUpdateFolder();
			verifyFolder();
			serializeFolder();
			System.out.println(log);
		} catch (ServerCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidWriteEnablerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImmutableFileExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteFileDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FailedToVerifySignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getIt() {
		try {
			readTestData();
			this.testSizeOfFakeAlias();
			testBlankFolderCreation();
			testUpdateFolder();
			verifyFolder();
			serializeFolder();
			return log;
		} catch (ServerCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidWriteEnablerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImmutableFileExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteFileDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FailedToVerifySignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void testBlankFolderCreation() {
		int n = 10;
		CSVFolder folder;
		long before = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			folder = new CSVFolder();
		}
		long after = System.currentTimeMillis();
		long diff = after - before;
		double avg = diff / n;
		log += "BlankFolder AVG: " + avg + "\n";
	}

	public void testUpdateFolder() throws ServerCommunicationException,
			InvalidWriteEnablerException, ImmutableFileExistsException,
			RemoteFileDoesNotExistException, FailedToVerifySignatureException {
		System.out.println("running test: Update folder");
		log += "Running test: Update folder (lines, timetotal)\n";

		CSVFolder folder;
		// this.manager.uploadFolder(folder);
		// folder = this.manager.downloadFolder(folder.getCapability());

		// int[] points = { 1, 10, 50, 100, 500, 750, 1000, 5000, 10000 };
		HashMap<String, Capability> fakeAliases;

		for (int i = 0; i < points.length; i++) {
			folder = new CSVFolder();
			folder.hack_do_not_create_plaintext = true;
			// fakeAliases = generateFakeAliases(points[i]);
			// folder.getContents().putAll(fakeAliases);

			for (int j = 0; j < points[i]; j++) {
				folder.addContent(aliases[j], caps[j]);
			}

			// Time Encryption/Signing
			folder.createPlainText();
			long before = System.currentTimeMillis();
			// byte[] tmp = folder.upload();
			folder.encrypt();
			folder.sign();
			long after = System.currentTimeMillis();
			long diff = after - before;
			System.out.println("Updatefolder point: " + points[i] + " time: "
					+ diff);
			log += points[i] + " " + diff + "\n";
			// folder = this.manager.downloadFolder(folder.getCapability());

		}

	}

	public void testSizeOfFakeAlias() {
		HashMap<String, Capability> fakeAliases = generateFakeAliases(1);
		String alias = fakeAliases.keySet().iterator().next();
		String cap = fakeAliases.get(alias).toString();
		String tot = alias + ";" + cap + "\n";
		log += "The sizer of 1 entry is: " + tot.getBytes().length + "bytes\n";
	}

	public void serializeFolder() {
		log += "Running test: Serialize folder (lines, timetotal)";
		System.out.println("running test: serialize folder");
		CSVFolder folder;
		// this.manager.uploadFolder(folder);
		// folder = this.manager.downloadFolder(folder.getCapability());

		// int[] points = { 1, 10, 50, 100, 500, 750, 1000, 5000, 10000 };
		HashMap<String, Capability> fakeAliases;

		for (int i = 0; i < points.length; i++) {
			folder = new CSVFolder();
			folder.hack_do_not_create_plaintext = true;
			// fakeAliases = generateFakeAliases(points[i]);
			// folder.getContents().putAll(fakeAliases);
			// Time Encryption/Signing
			for (int j = 0; j < points[i]; j++) {
				folder.addContent(aliases[j], caps[j]);
			}

			long before = System.currentTimeMillis();
			// byte[] tmp = folder.upload();
			// folder.encrypt();
			// folder.sign();
			folder.createPlainText();
			long after = System.currentTimeMillis();
			long diff = after - before;
			System.out.println("Updatefolder point: " + points[i] + " time: "
					+ diff);
			log += points[i] + " " + diff + "\n";
			// folder = this.manager.downloadFolder(folder.getCapability());

		}
	}

	public void verifyFolder() throws ServerCommunicationException,
			RemoteFileDoesNotExistException, FailedToVerifySignatureException,
			InvalidWriteEnablerException, ImmutableFileExistsException {
		log += "Running test: Verify folder (lines, timetotal)\n";
		System.out.println("running test: Verify folder");
		CSVFolder folder = new CSVFolder();

		HashMap<String, Capability> fakeAliases;

		for (int i = 0; i < points.length; i++) {
			folder = new CSVFolder();

			// fakeAliases = generateFakeAliases(points[i]);
			// folder.getContents().putAll(fakeAliases);
			for (int j = 0; j < points[i]; j++) {
				folder.addContent(aliases[j], caps[j]);
			}

			this.manager.uploadFolder(folder);
			folder = this.manager.downloadFolder(folder.getCapability());

			long before = System.currentTimeMillis();
			boolean b = folder.isValid();
			long after = System.currentTimeMillis();
			long diff = after - before;
			if (b == false) {
				System.out.println("OMG FAILED VERIFY!");
			}
			System.out.println("VerifyFolder point: " + points[i] + " time: "
					+ diff);
			log += points[i] + " " + diff + "\n";
		}

	}

	private HashMap<String, Capability> generateFakeAliases(int n) {

		Capability cap;
		String alias;
		HashMap<String, Capability> fakeAliases = new HashMap<String, Capability>();

		for (int i = 0; i < n; i++) {
			alias = Base32.encode(random16bytes());
			cap = new CapabilityImpl(CapabilityType.RO, random16bytes(),
					random16bytes(), true);
			fakeAliases.put(alias, cap);
		}
		return fakeAliases;

	}

	private byte[] random16bytes() {
		String foo = "" + Math.random() * 100000;
		byte[] tmp = Cryptoutil.hash(foo.getBytes(), 16);
		return tmp;
	}

	public static void main(String[] args) throws IOException {
		Communication com = CommunicationFactory
				.createCommunicationFromProperties();
		CSVFileManager manager = new CSVFileManager(com);
		TestSpeedOfFolders tsof = new TestSpeedOfFolders(manager);
		// tsof.createTestData();
		tsof.doIt();
	}

	public void createTestData() throws IOException {
		OutputStream os = new FileOutputStream("/tmp/data.txt");
		int highvalue = 10000;
		HashMap<String, Capability> foo = generateFakeAliases(highvalue);
		CSVFolder folder = new CSVFolder();
		folder.getContents().putAll(foo);

		folder.createPlainText();
		os.write(folder.plainText);
		os.flush();
		os.close();
	}

}
