package no.ntnu.item.csv.capability;

import javax.crypto.KeyGenerator;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

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
		this.cap = new CapabilityImpl(CapabilityType.RO, Cryptoutil.generateSymmetricKey().getEncoded(), null);
	}
	
	@After
	public void teardown() {
		
	}
	
	@Test
	public void testBase64() {
		Assert.assertEquals(26, this.cap.getStorageIndex().length());
		//System.out.println(this.cap.getStorageIndex());
	}
	
	@Test
	public void testEncodingAndDecoding() {
		String encoded = this.cap.toString();
		Capability decoded = CapabilityImpl.fromString(encoded);
		Assert.assertArrayEquals(this.cap.getKey(), decoded.getKey());
		Assert.assertEquals(this.cap.getType(), decoded.getType());
		Assert.assertEquals(this.cap.getStorageIndex(), decoded.getStorageIndex());
		Assert.assertEquals(this.cap.getVerificationKey(), decoded.getVerificationKey());
	}
	
}
