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
	private int defaultPort = 80;
	private HttpHost serverHost;
	private final String SERVER_PUT = "put/";
	private final String SERVER_GET = "get/";

	private String username;
	private String password;

	public Communication(String serverAddress, String username, String password) {
		this.serverHost = new HttpHost(serverAddress, defaultPort, "http");
		this.username = username;
		this.password = password;
	}

	public Communication(String serverAddress) {
		this.serverHost = new HttpHost(serverAddress, defaultPort, "http");
	}

	public boolean testLogin() throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();

		client.getCredentialsProvider().setCredentials(
				new AuthScope(this.serverHost.getHostName(),
						this.serverHost.getPort()),
				new UsernamePasswordCredentials(this.username, this.password));

		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(this.serverHost, basicAuth);

		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

		HttpGet httpget = new HttpGet("/");
		HttpResponse response = client.execute(this.serverHost, httpget,
				localcontext);

		StatusLine responseStatus = response.getStatusLine();
		System.out.println("Testing login: " + responseStatus.toString());

		client.getConnectionManager().shutdown();
		return responseStatus.getStatusCode() == 200;
	}

	public int put(CSVObject object) {
		String putAddress = "http://" + this.serverHost.getHostName() + "/"
				+ this.SERVER_PUT;

		if (object == null)
			throw new NullPointerException();

		HttpClient client = new DefaultHttpClient();

		HttpPut put;
		if (object instanceof CSVFolder) {
			put = new HttpPut(putAddress
					+ object.getCapability().getStorageIndex() + "/"
					+ Base32.encode(object.getCapability().getWriteEnabler()));
		} else {
			put = new HttpPut(putAddress
					+ object.getCapability().getStorageIndex());
		}

		ByteArrayEntity entity = new ByteArrayEntity(object.getTransferArray());

		put.setEntity(entity);

		HttpResponse response;
		int code;
		try {
			response = client.execute(put);
			code = response.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			code = 0;
		} catch (IOException e) {
			e.printStackTrace();
			code = 0;
		}

		return code;
	}

	public byte[] get(String index) throws RemoteFileDoesNotExistException,
			ServerCommunicationException {
		String getAddress = "http://" + this.serverHost.getHostName() + "/"
				+ this.SERVER_GET;

		if (index == null)
			throw new NullPointerException();

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(getAddress + index);

		byte[] bytes = null;
		HttpResponse response;
		try {
			response = client.execute(get);
			System.out.println("RESPONSE: "
					+ response.getStatusLine().getStatusCode());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
