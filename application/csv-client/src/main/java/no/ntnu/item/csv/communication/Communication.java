package no.ntnu.item.csv.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import no.ntnu.item.csv.csvobject.CSVObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class Communication {

    public static int put(CSVObject object, String serv_addr) throws IOException
    {
    	if(object == null || serv_addr == null)
    		return 400;
    	
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(serv_addr);
    	
    	ByteArrayBody file = new ByteArrayBody(object.getCipherText(), object.getCapability().getStorageIndex().toString());
//    	FileBody file = new FileBody(new File("/home/melvold/Desktop/test.txt"));
    	MultipartEntity reqEnt = new MultipartEntity();
    	reqEnt.addPart("encrypted_file", file);
    	post.setEntity(reqEnt);
    	
    	HttpResponse response = client.execute(post);
    	
//    	HttpEntity respEnt = response.getEntity(); 	
//    	if (respEnt != null) {
//    	    InputStream is = respEnt.getContent();
//    	    int l;
//    	    byte[] tmp = new byte[2048];
//    	    while ((l = is.read(tmp)) != -1);
//    	    is.close();
//    	    String value = new String(tmp);
//            
//            System.out.println(value);
//    	}
    	
    	return response.getStatusLine().getStatusCode();
    }
    
    public byte[] get(String index, String serv_addr) throws ClientProtocolException, IOException
    {
    	if(index == null || serv_addr == null)
    		return null;
    	
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(serv_addr);
    	
    	StringBody str_ind = new StringBody(index);
    	MultipartEntity reqEnt = new MultipartEntity();
    	reqEnt.addPart("storage_index", str_ind);
    	
    	HttpResponse response = client.execute(post);
    	
    	InputStream is = response.getEntity().getContent();
    	ArrayList<Byte> tmp = new ArrayList<Byte>();
    	
    	int nb;
    	while((nb = is.read()) != -1)
    		tmp.add((byte)nb);
    	
    	byte[] bytes = new byte[tmp.size()];
    	
    	for(int i = 0; i < bytes.length; i++)
    		bytes[i] = tmp.get(i);

    	return bytes;
    }
    
    public static void main(String[] args) throws IOException{
//    	InputStream is = new FileInputStream(new File("/home/melvold/Desktop/test.txt"));
//    	
//    	int nb;
//    	while((nb = is.read()) != -1){
//    		System.out.println(nb);
//    	}
    	
//    	byte[] b = get("index", "http://129.241.205.111/test/");
//    	if(b != null){
//	    	for(int i = 0; i < b.length; i++){
//	    		System.out.println((int)b[i]);
//	    	}
//    	}
    	
//    	put(null, "http://129.241.205.111/put");
    }
}
