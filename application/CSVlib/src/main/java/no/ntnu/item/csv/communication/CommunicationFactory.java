package no.ntnu.item.csv.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommunicationFactory {

	public static Communication createCommunicationFromProperties() {
		Properties properties = new Properties();
		try {
			InputStream is = CommunicationFactory.class
					.getResourceAsStream("/configuration.properties");
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load properties file");
		}

		String username = properties.getProperty("USERNAME");
		String password = properties.getProperty("PASSWORD");
		String server = properties.getProperty("SERVER");

		return new Communication(server, username, password);

	}

}
