package no.ntnu.item.exception;

public class CloudServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CloudServiceException(String e) {
		super(e);
	}
}
