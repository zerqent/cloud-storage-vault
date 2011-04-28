package no.ntnu.item.csv.exception;

public class InvalidWriteEnablerException extends CSVException {

	private static final long serialVersionUID = 1L;

	public InvalidWriteEnablerException(String msg) {
		super(msg);
	}

	public InvalidWriteEnablerException() {
		super("The specified write enabler is invalid");
	}

}
