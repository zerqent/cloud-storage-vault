package no.ntnu.item.csv.communication;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

public class Communication {

	private int port = 8080;

	private HttpHost serverHost;
	private final String SERVER_PUT = "/put/";
	private final String SERVER_GET = "/get/";

	private String username;
	private String password;

	public Communication(String serverAddress, String username, String password) {
		this.serverHost = new HttpHost(serverAddress, this.port, "http");
		this.username = username;
		this.password = password;
	}

	public Communication(String serverAddress) {
		this.serverHost = new HttpHost(serverAddress, this.port, "http");
	}

	public Communication(String serverAddress, int port) {
		this.port = port;
		this.serverHost = new HttpHost(serverAddress, port, "http");
	}

	public boolean testLogin() throws ClientProtocolException, IOException {
		DefaultHttpClient client = getNewBasicAuthHttpClient();

		HttpGet httpget = new HttpGet("/");

		HttpResponse response = client.execute(this.serverHost, httpget,
				getAuthCacheContext());
		StatusLine responseStatus = response.getStatusLine();

		System.out.println("Testing login: " + responseStatus.toString());

		client.getConnectionManager().shutdown();

		return responseStatus.getStatusCode() == 200;
	}

	private DefaultHttpClient getNewBasicAuthHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();

		HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
			@Override
			public void process(final HttpRequest request,
					final HttpContext context) throws HttpException,
					IOException {
				AuthState authState = (AuthState) context
						.getAttribute(ClientContext.TARGET_AUTH_STATE);
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context
						.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

				if (authState.getAuthScheme() == null) {
					AuthScope authScope = new AuthScope(
							targetHost.getHostName(), targetHost.getPort());
					Credentials creds = credsProvider.getCredentials(authScope);
					if (creds != null) {
						authState.setAuthScheme(new BasicScheme());
						authState.setCredentials(creds);
					}
				}
			}
		};

		addBasicAuth(client);
		client.addRequestInterceptor(preemptiveAuth, 0);

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

	public int putInputStream(String url, InputStream is, long streamLength) {
		InputStreamEntity entity = new InputStreamEntity(is, streamLength);
		return putEntity(url, entity);
	}

	public int putByteArray(String url, byte[] byteArray) {
		HttpEntity entity = new ByteArrayEntity(byteArray);
		return putEntity(url, entity);
	}

	private int putEntity(String url, HttpEntity entity) {
		DefaultHttpClient client = getNewBasicAuthHttpClient();

		HttpPut put;

		put = new HttpPut(this.SERVER_PUT + url);
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

	public HttpResponse get(String url) {
		HttpClient client = getNewBasicAuthHttpClient();
		HttpGet get = new HttpGet(this.SERVER_GET + url);

		HttpResponse response = null;
		try {
			response = client.execute(this.serverHost, get,
					getAuthCacheContext());
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return response;

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
