package no.ntnu.item;

import no.ntnu.item.exception.CloudServiceException;
import no.ntnu.item.provider.CloudFileManager;
import no.ntnu.item.provider.amazons3.AmazonS3FileManager;

public class Configuration {
	
	/** Configuration */
	public CloudFileManager cloudFileManager;
	
	private static Configuration self = null; 
	
	public static Configuration getConfiguration() {
		if (self==null) {
			self = new Configuration();
		}
		return self;
	}
	
	private Configuration() {
		try {
			this.cloudFileManager = new AmazonS3FileManager();
		} catch (CloudServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
