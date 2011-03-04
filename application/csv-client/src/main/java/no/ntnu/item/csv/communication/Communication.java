package no.ntnu.item.csv.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.item.csv.csvobject.CSVObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class Communication {

    public static int put(CSVObject object, String serv_addr) throws IOException
    {
    	if(object == null || serv_addr == null)
    		return 600;
    	
    	
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(serv_addr);
    	
    	ByteArrayBody file = new ByteArrayBody(object.getTransferArray(), object.getCapability().getStorageIndex());
    	
    	MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    	entity.addPart("encrypted_file", file);
    	post.setEntity(entity);
    	
    	HttpResponse response = client.execute(post);
  	
    	HttpEntity respEnt = response.getEntity(); 	
    	if (respEnt != null) {
    	    InputStream is = respEnt.getContent();
    	    int l;
    	    byte[] tmp = new byte[2048];
    	    while ((l = is.read(tmp)) != -1);
    	    is.close();
    	    String value = new String(tmp);
            
            System.out.println(value);
    	}
    	
    	return response.getStatusLine().getStatusCode();
    }
    
    public static byte[] get(String index, String serv_addr) throws ClientProtocolException, IOException
    {
    	if(index == null || serv_addr == null)
    		return null;
    	
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(serv_addr);

    	List <NameValuePair> nvp = new ArrayList <NameValuePair>();
    	nvp.add(new BasicNameValuePair("storage_index", index));
    	post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
    	
    	HttpResponse response = client.execute(post);
    	
    	InputStream is = response.getEntity().getContent();
    	ArrayList<Byte> tmp = new ArrayList<Byte>();
//    	int l;
//    	byte[] tmp = new byte[2048];
//	    while ((l = is.read(tmp)) != -1);
//	    is.close();
//	    String value = new String(tmp);
//        
//        System.out.println(value);
        
    	int nb;
    	while((nb = is.read()) != -1)
    		tmp.add((byte)nb);
    	
    	byte[] bytes = new byte[tmp.size()];
    	
    	for(int i = 0; i < bytes.length; i++)
    		bytes[i] = tmp.get(i);

    	return bytes;
    }
    
//    public static void main(String[] args) throws IOException{   	
//
////    	CSVFileFacade file = new CSVFileFacade();
////    	file.setPlainText(new File("/home/melvold/Desktop/test.txt"));
////    	file.encrypt();
////    	
////    	put(file, "http://129.241.205.111/put");
//    	
////    	byte[] b = get("", "http://129.241.205.111/get");
////    	if(b != null){
////	    	for(int i = 0; i < b.length; i++){
////	    		System.out.println((int)b[i]);
////	    	}
////    	}
//    }
}
