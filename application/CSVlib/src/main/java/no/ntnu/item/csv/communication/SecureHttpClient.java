package no.ntnu.item.csv.communication;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

public class SecureHttpClient extends DefaultHttpClient {
	// TODO: At the time this needs to be the correct certificate of the server
	public final String createCert = "-----BEGIN CERTIFICATE-----\n"
			+ "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\n"
			+ "Fbie9s/Qs1M+6Vhy7WfBBTzy7O0YbCalAw==\n"
			+ "-----END CERTIFICATE-----\n";

	public SecureHttpClient() {
		super();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", newSslSocketFactory(), 443));
		return new SingleClientConnManager(getParams(), registry);
	}

	private SSLSocketFactory newSslSocketFactory() {
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);

			TrustedCertificateEntry certEntry = new TrustedCertificateEntry(
					generateCertificate());
			ks.setCertificateEntry("create.q2s",
					certEntry.getTrustedCertificate());

			return new SSLSocketFactory(ks);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}

	private X509Certificate generateCertificate() {
		ByteArrayInputStream is = new ByteArrayInputStream(
				this.createCert.getBytes());
		CertificateFactory cf;
		X509Certificate cert = null;
		try {
			cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(is);
		} catch (CertificateException e) {
			e.printStackTrace();
		}

		return cert;
	}
}
