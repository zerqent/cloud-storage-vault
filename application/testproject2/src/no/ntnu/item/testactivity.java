package no.ntnu.item;

import no.ntnu.item.provider.CloudProvider;
import no.ntnu.item.provider.amazons3.AmazonS3Provider;
import android.app.Activity;
import android.os.Bundle;

public class testactivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {	
    	System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
        super.onCreate(savedInstanceState);
        System.out.println("Hello World");
        CloudProvider amazon = new AmazonS3Provider();
        setContentView(R.layout.main);
    }
}
