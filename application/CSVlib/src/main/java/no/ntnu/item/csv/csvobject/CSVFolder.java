package no.ntnu.item.csv.csvobject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.ntnu.item.cryptoutil.Cryptoutil;
import no.ntnu.item.csv.capability.Capability;
import no.ntnu.item.csv.capability.CapabilityImpl;
import no.ntnu.item.csv.capability.CapabilityType;

public class CSVFolder implements CSVObject {

	private PublicKey pubkey;
	private PrivateKey privkey;
	private byte[] encPrivKey;

	private Capability capability;
	private Map<String, Capability> contents;

	private byte[] ciphertext;
	private byte[] plainText;
	private byte[] iv;

	private byte[] signature;

	public CSVFolder() {
		generateKeys();
		this.contents = new HashMap<String, Capability>();
	}

	public CSVFolder(Capability capability) {
		this.capability = capability;
	}

	public void download(byte[] packet) {
		byte[] pubkey = new byte[Cryptoutil.ASYM_SIZE / 8 + 1 + 3];
		System.arraycopy(packet, 0, pubkey, 0, pubkey.length);
		this.setPubKey(pubkey);

		byte[] signature = new byte[Cryptoutil.ASYM_SIZE / 8];
		System.arraycopy(packet, pubkey.length, signature, 0, signature.length);
		this.signature = signature;

		byte[] iv = new byte[Cryptoutil.SYM_BLOCK_SIZE / 8];
		System.arraycopy(packet, pubkey.length + signature.length, iv, 0,
				iv.length);
		this.iv = iv;

		byte[] encPrivKey = new byte[2 * (Cryptoutil.ASYM_SIZE / 8) + 1 + 15];
		System.arraycopy(packet, pubkey.length + signature.length + iv.length,
				encPrivKey, 0, encPrivKey.length);
		this.encPrivKey = encPrivKey;

		byte[] cipherText = new byte[packet.length - pubkey.length
				- signature.length - iv.length - encPrivKey.length];
		System.arraycopy(packet, pubkey.length + signature.length + iv.length
				+ encPrivKey.length, cipherText, 0, cipherText.length);
		this.ciphertext = cipherText;

		byte tmp[] = Cryptoutil.symECBDecrypt(encPrivKey, new SecretKeySpec(
				this.getCapability().getKey(), Cryptoutil.SYM_CIPHER));
		this.privkey = Cryptoutil.createRSAPrivateKey(tmp);
		this.decrypt();
		// this.ciphertext = null;
		// this.signature = null;

	}

	private void generateKeys() {
		KeyPair pair = Cryptoutil.generateAsymmetricKeys();
		this.pubkey = pair.getPublic();
		this.privkey = pair.getPrivate();

		byte[] write = Cryptoutil.hash(this.privkey.getEncoded(),
				Cryptoutil.SYM_SIZE / 8);
		// byte[] read = Cryptoutil.hash(write, 16);
		RSAPublicKey pub = (RSAPublicKey) this.pubkey;
		byte[] mod = pub.getModulus().toByteArray();
		byte[] pubexp = pub.getPublicExponent().toByteArray();
		byte[] to_hash = new byte[mod.length + pubexp.length];
		System.arraycopy(mod, 0, to_hash, 0, mod.length);
		System.arraycopy(pubexp, 0, to_hash, mod.length, pubexp.length);

		byte[] verify = Cryptoutil.hash(to_hash, Cryptoutil.SYM_SIZE / 8);

		Capability writecap = new CapabilityImpl(CapabilityType.RW, write,
				verify, false);
		this.capability = writecap;
	}

	private void sign() {
		assert this.ciphertext != null;
		byte[] hash = Cryptoutil.hash(this.ciphertext, -1);
		this.signature = Cryptoutil.signature(hash, this.privkey);
	}

	private void encrypt() {
		byte[] read;
		if (this.capability.getType() == CapabilityType.RW) {
			read = Cryptoutil.hash(this.capability.getKey(),
					Cryptoutil.SYM_SIZE / 8);
		} else {
			read = this.capability.getKey();
		}
		this.createPlainText();
		SecretKeySpec sks = new SecretKeySpec(read, Cryptoutil.SYM_CIPHER);
		this.iv = Cryptoutil.generateIV();
		this.ciphertext = Cryptoutil.symEncrypt(this.plainText, sks,
				new IvParameterSpec(this.iv));
		sign();
		if (this.encPrivKey == null) {
			this.encPrivKey = Cryptoutil.symECBEncrypt(Cryptoutil
					.serializePrivateKey((RSAPrivateKey) this.privkey),
					new SecretKeySpec(this.capability.getKey(),
							Cryptoutil.SYM_CIPHER));
		}

	}

	private void decrypt() {
		assert this.ciphertext != null;
		byte[] read;
		if (this.capability.getType() == CapabilityType.RW) {
			read = Cryptoutil.hash(this.capability.getKey(),
					Cryptoutil.SYM_SIZE / 8);
		} else {
			read = this.capability.getKey();
		}
		SecretKeySpec sks = new SecretKeySpec(read, Cryptoutil.SYM_CIPHER);
		this.plainText = Cryptoutil.symDecrypt(this.ciphertext, sks,
				new IvParameterSpec(this.iv));

		this.createContentsFromPlainText();
	}

	@Override
	public boolean isValid() {
		byte[] verify = this.capability.getVerificationKey();
		byte[] fromServer = this.getPublicKeyHash();
		int n = fromServer.length;

		if (verify.length != fromServer.length) {
			return false;
		}

		for (int i = 0; i < n; i++) {
			if (verify[i] != fromServer[i]) {
				return false;
			}
		}

		byte[] hash = Cryptoutil.hash(this.ciphertext, -1);
		return Cryptoutil.signature_valid(this.signature, hash, this.pubkey);
	}

	protected byte[] getCipherText() {
		return this.ciphertext;
	}

	@Override
	public Capability getCapability() {
		return this.capability;
	}

	@Override
	public void setCapability(Capability capability) {
		this.capability = capability;
	}

	public Map<String, Capability> getContents() {
		if (this.contents == null && this.ciphertext != null) {
			decrypt();
		}
		return this.contents;
	}

	protected void setPubKey(byte[] pubKey) {
		this.pubkey = Cryptoutil.createRSAPublicKey(pubKey);
	}

	public void addContent(String alias, Capability capability) {
		this.contents.put(alias, capability);

	}

	protected byte[] getPubKey() {
		return Cryptoutil.serializePublicKey((RSAPublicKey) this.pubkey);
	}

	public byte[] upload() {
		this.encrypt();
		this.sign();

		byte[] pub = Cryptoutil.serializePublicKey((RSAPublicKey) this.pubkey);

		byte[] transfer = new byte[pub.length + this.signature.length
				+ this.iv.length + this.encPrivKey.length
				+ this.ciphertext.length];

		System.arraycopy(pub, 0, transfer, 0, pub.length);
		System.arraycopy(this.signature, 0, transfer, pub.length,
				this.signature.length);
		System.arraycopy(this.iv, 0, transfer, pub.length
				+ this.signature.length, this.iv.length);
		System.arraycopy(this.encPrivKey, 0, transfer, pub.length
				+ this.signature.length + this.iv.length,
				this.encPrivKey.length);
		System.arraycopy(this.ciphertext, 0, transfer, pub.length
				+ this.signature.length + this.iv.length
				+ this.encPrivKey.length, this.ciphertext.length);

		return transfer;
	}

	protected void createContentsFromPlainText() {
		Map<String, Capability> contents = new HashMap<String, Capability>();
		String strCont = new String(this.plainText);
		String[] lines = strCont.split("\n");

		if (lines.length == 1 && lines[0].length() < 1) {
			this.contents = contents;
			return;
		}

		for (int i = 0; i < lines.length; i++) {
			String[] lineCont = lines[i].split(";");
			Capability cap = CapabilityImpl.fromString(lineCont[1]);
			String alias = lineCont[0];
			contents.put(alias, cap);
		}
		this.contents = contents;
	}

	protected void createPlainText() {
		if (this.contents != null) {
			String plaintext = "";
			for (Iterator<String> iterator = this.getContents().keySet()
					.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				Capability cap = this.getContents().get(key);
				plaintext += key + ";" + cap.toString() + "\n";
			}
			this.plainText = plaintext.getBytes();
		}
	}

	public byte[] getPublicKeyHash() {
		RSAPublicKey pub = (RSAPublicKey) this.pubkey;
		byte[] mod = pub.getModulus().toByteArray();
		byte[] pubexp = pub.getPublicExponent().toByteArray();
		byte[] to_hash = new byte[mod.length + pubexp.length];
		System.arraycopy(mod, 0, to_hash, 0, mod.length);
		System.arraycopy(pubexp, 0, to_hash, mod.length, pubexp.length);
		return Cryptoutil.hash(to_hash, Cryptoutil.SYM_SIZE / 8);
	}

}
