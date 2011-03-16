package no.ntnu.item.csv.cryptoutil;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.CSVActivity;
import android.test.ActivityInstrumentationTestCase2;

public class CryptoSpeedTest extends
		ActivityInstrumentationTestCase2<CSVActivity> {

	private KeyPair keyPair = Cryptoutil.generateAsymmetricKeys();
	private SecretKey secretKey = Cryptoutil.generateSymmetricKey();
	private byte[] msg = new String("hello world this is a test").getBytes();
	private byte[] hashOfmsg = Cryptoutil.hash(msg, 16);
	private byte[] signOfMsg = Cryptoutil.signature(this.hashOfmsg,
			this.keyPair.getPrivate());
	private byte[] cipherText = Cryptoutil.symECBEncrypt(this.msg,
			this.secretKey);;
	private IvParameterSpec iv = new IvParameterSpec(Cryptoutil.generateIV());
	private byte[] privSer = Cryptoutil
			.serializePrivateKey((RSAPrivateKey) this.keyPair.getPrivate());
	private byte[] pubSer = Cryptoutil
			.serializePublicKey((RSAPublicKey) this.keyPair.getPublic());

	public CryptoSpeedTest() {
		super("no.ntnu.item.csv", CSVActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.keyPair = Cryptoutil.generateAsymmetricKeys();
		this.secretKey = Cryptoutil.generateSymmetricKey();
	}

	public void testspeedOfSingleHash() {
		Cryptoutil.hash(this.msg, -1);
	}

	public void testspeedOfSingleHashWithTruncate() {
		Cryptoutil.hash(this.msg, 16);
	}

	public void testspeedOfDoubleHashWithTruncate() {
		Cryptoutil.nHash(this.msg, 2, 16);
	}

	public void testspeedOfRSAKeyGeneration() {
		Cryptoutil.generateAsymmetricKeys();
	}

	public void testspeedOfAESKeyGeneration() {
		Cryptoutil.generateSymmetricKey();
	}

	public void testspeedOfSignature() {
		Cryptoutil.signature(this.hashOfmsg, this.keyPair.getPrivate());
	}

	public void testspeedOfVerifySignature() {
		Cryptoutil.signature_valid(this.signOfMsg, this.hashOfmsg,
				this.keyPair.getPublic());
	}

	public void testspeedOfECBEncryption() {
		Cryptoutil.symECBEncrypt(this.msg, this.secretKey);
	}

	public void testspeedOfIVGeneration() {
		Cryptoutil.generateIV();
	}

	public void testspeedOfCBCEncryption() {
		Cryptoutil.symEncrypt(this.msg, this.secretKey, this.iv);
	}

	public void testspeedOfCBCDecryption() {
		Cryptoutil.symDecrypt(this.cipherText, this.secretKey, this.iv);
	}

	public void testspeedOfRSAPrivateKeySerialization() {
		Cryptoutil.serializePrivateKey((RSAPrivateKey) this.keyPair
				.getPrivate());
	}

	public void testspeedOfRSAPrivateKeyDeSerialization() {
		Cryptoutil.createRSAPrivateKey(this.privSer);
	}

	public void testspeedOfRSAPublicKeySerialization() {
		Cryptoutil.serializePublicKey((RSAPublicKey) this.keyPair.getPublic());
	}

	public void testspeedOfRSAPublicKeyDeSerialization() {
		Cryptoutil.createRSAPublicKey(this.pubSer);
	}
}
