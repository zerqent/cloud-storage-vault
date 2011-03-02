package no.item.csv.capability;

import javax.crypto.KeyGenerator;

import no.ntnu.item.csv.capability.CSVKey;
import no.ntnu.item.csv.capability.CSVKeyImpl;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;
import no.ntnu.item.csv.capability.KeyType;

import org.apache.commons.codec.binary.Base64;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CapabilityImplTest {
	
	private CapabilityImpl cap;
	
	@Before
	public void setUp() throws Exception {
		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		keygen.init(128);
		CSVKey key = new CSVKeyImpl(KeyType.READ_KEY,keygen.generateKey().getEncoded());
		this.cap = new CapabilityImpl(key, CapabilityType.READ_ONLY);
	}
	
	@After
	public void teardown() {
		
	}
	
	@Test
	public void testBase64() {
		Assert.assertEquals(46, this.cap.getStorageIndex().length());
		// TODO: Perhaps test some more, change size
	}
	
}
