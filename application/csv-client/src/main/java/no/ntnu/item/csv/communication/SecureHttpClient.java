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
	private final String createCert = "-----BEGIN CERTIFICATE-----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-----END CERTIFICATE-----";

	public SecureHttpClient() {
		super();
	}

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		// registry.register(new Scheme("http", PlainSocketFactory
		// .getSocketFactory(), 80));
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
