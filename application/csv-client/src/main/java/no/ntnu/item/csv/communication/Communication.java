package no.ntnu.item.csv.communication;

import java.io.IOException;
import java.io.InputStream;

import no.ntnu.item.csv.contrib.jonelo.sugar.util.Base32;
import no.ntnu.item.csv.csvobject.CSVFolder;
import no.ntnu.item.csv.csvobject.CSVObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class Communication {

	public static final String SERVER_PUT = "http://129.241.205.111/put";
	public static final String SERVER_GET = "http://129.241.205.111/get";

	public static int put(CSVObject object, String serv_addr) {
		if (object == null || serv_addr == null)
			throw new NullPointerException();

		HttpClient client = new DefaultHttpClient();
		// HttpPost post = new HttpPost(serv_addr);
		HttpPut put;
		if (object instanceof CSVFolder) {
			put = new HttpPut(serv_addr + "/"
					+ object.getCapability().getStorageIndex() + "/"
					+ Base32.encode(object.getCapability().getWriteEnabler()));
		} else {
			put = new HttpPut(serv_addr + "/"
					+ object.getCapability().getStorageIndex());
		}

		// MultipartEntity entity = new
		// MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		ByteArrayEntity entity = new ByteArrayEntity(object.getTransferArray());
		// ByteArrayBody file = new ByteArrayBody(object.getTransferArray(),
		// object.getCapability().getStorageIndex());
		// entity.addPart("encrypted_file", file);

		put.setEntity(entity);

		HttpResponse response;
		int code;
		try {
			response = client.execute(put);
			code = response.getStatusLine().getStatusCode();

			// HttpEntity respEnt = response.getEntity();
			// if (respEnt != null) {
			// InputStream is = respEnt.getContent();
			// int l;
			// byte[] tmp = new byte[2048];
			// while ((l = is.read(tmp)) != -1);
			// is.close();
			// String value = new String(tmp);
			//
			// System.out.println(value);
			// }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			code = 0;
		} catch (IOException e) {
			e.printStackTrace();
			code = 0;
		}

		return code;
	}

	public static byte[] get(String index, String serv_addr) {
		if (index == null || serv_addr == null)
			throw new NullPointerException();

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(serv_addr + "/" + index);

		// List<NameValuePair> nvp = new ArrayList<NameValuePair>();
		// nvp.add(new BasicNameValuePair("storage_index", index));
		// try {
		// post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		byte[] bytes = null;
		HttpResponse response;
		try {
			response = client.execute(get);

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

	// public static void main(String[] args) throws IOException{
	//
	// // CSVFileFacade file = new CSVFileFacade();
	// // file.setPlainText(new File("/home/melvold/Desktop/test.txt"));
	// // file.encrypt();
	// //
	// // put(file, "http://129.241.205.111/put");
	//
	// // byte[] b = get("prof.jpeg", "http://129.241.205.111/get");
	// // if(b != null){
	// // for(int i = 0; i < b.length; i++){
	// // System.out.println((int)b[i]);
	// // }
	// // }
	// }
}
