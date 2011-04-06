package no.ntnu.item.csv.exception;

public class ImmutableFileExistsException extends CSVException {

	private static final long serialVersionUID = 1L;

	public ImmutableFileExistsException() {
		super();
	}

	public ImmutableFileExistsException(String msg) {
		super(msg);
	}

}
