package no.ntnu.item.csv.exception;

public class FailedToVerifySignatureException extends CSVException {

	private static final long serialVersionUID = 1L;

	public FailedToVerifySignatureException(String alias) {
		super("Failed to verify signature for " + alias);
	}

	public FailedToVerifySignatureException() {
		super("Failed to verify the signature of object");
	}

}
