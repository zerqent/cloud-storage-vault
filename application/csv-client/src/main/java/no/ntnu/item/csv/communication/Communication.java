package no.ntnu.item.csv.communication;

import java.io.IOException;
import java.io.InputStream;

import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.CSVObject;
import no.ntnu.item.csv.exception.RemoteFileDoesNotExistException;
import no.ntnu.item.csv.exception.ServerCommunicationException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

public class Communication {
	private int port = 443;

	private HttpHost serverHost;
	private final String SERVER_PUT = "/put/";
	private final String SERVER_GET = "/get/";

	private String username;
	private String password;

	public Communication(String serverAddress, String username, String password) {
		this.serverHost = new HttpHost(serverAddress, this.port, "https");
		this.username = username;
		this.password = password;
	}

	public Communication(String serverAddress) {
		this.serverHost = new HttpHost(serverAddress, this.port, "https");
	}

	public Communication(String serverAddress, int port) {
		this.port = port;
		this.serverHost = new HttpHost(serverAddress, port, "https");
	}

	public boolean testLogin() throws ClientProtocolException, IOException {
		SecureHttpClient client = getNewSecureAuthHttpClient();

		HttpGet httpget = new HttpGet("/");

		HttpResponse response = client.execute(this.serverHost, httpget,
				getAuthCacheContext());
		StatusLine responseStatus = response.getStatusLine();

		System.out.println("Testing login: " + responseStatus.toString());

		client.getConnectionManager().shutdown();

		return responseStatus.getStatusCode() == 200;
	}

	public static void main(String[] arg) {
		Communication cs = new Communication("create.q2s.ntnu.no", "palru",
				"test123");
		try {
			cs.testLogin();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private SecureHttpClient getNewSecureAuthHttpClient() {
		SecureHttpClient client = new SecureHttpClient();
		addBasicAuth(client);

		return client;
	}

	private DefaultHttpClient getNewBasicAuthHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		addBasicAuth(client);

		return client;
	}

	private void addBasicAuth(DefaultHttpClient client) {
		client.getCredentialsProvider().setCredentials(
				new AuthScope(this.serverHost.getHostName(),
						this.serverHost.getPort()),
				new UsernamePasswordCredentials(this.username, this.password));
		return;
	}

	private BasicHttpContext getAuthCacheContext() {
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(this.serverHost, basicAuth);

		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

		return localcontext;
	}

	public int put(CSVObject object) {
		if (object == null)
			throw new NullPointerException();

		DefaultHttpClient client = getNewSecureAuthHttpClient();

		HttpPut put;
		if (object instanceof CSVFolder) {
			put = new HttpPut(this.SERVER_PUT
					+ object.getCapability().getStorageIndex() + "/"
					+ Base32.encode(object.getCapability().getWriteEnabler()));
		} else {
			put = new HttpPut(this.SERVER_PUT
					+ object.getCapability().getStorageIndex());
		}

		ByteArrayEntity entity = new ByteArrayEntity(object.getTransferArray());

		put.setEntity(entity);

		HttpResponse response;
		int code;
		try {
			response = client.execute(this.serverHost, put,
					getAuthCacheContext());
			code = response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			code = 0;
		} catch (IOException e) {
			e.printStackTrace();
			code = 0;
		} finally {
			client.getConnectionManager().shutdown();
		}

		return code;
	}

	public byte[] get(String index) throws RemoteFileDoesNotExistException,
			ServerCommunicationException {
		if (index == null)
			throw new NullPointerException();

		HttpClient client = getNewSecureAuthHttpClient();
		HttpGet get = new HttpGet(this.SERVER_GET + index);

		byte[] bytes = null;
		HttpResponse response;
		try {
			response = client.execute(this.serverHost, get,
					getAuthCacheContext());
			System.out.println("RESPONSE: "
					+ response.getStatusLine().toString());
			switch (response.getStatusLine().getStatusCode()) {
			case 200:
				break;
			case 201:
				break;
			case 202:
				break;
			case 204:
				break;
			case 404:
				throw new RemoteFileDoesNotExistException();
			default:
				throw new ServerCommunicationException();
			}

			InputStream is = response.getEntity().getContent();
			int len = Integer.parseInt(response
					.getFirstHeader("Content-Length").getValue());
			bytes = new byte[len];

			int nb;
			for (int i = 0; (nb = is.read()) != -1; i++)
				bytes[i] = (byte) nb;
			is.close();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}

		return bytes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
