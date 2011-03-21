package no.ntnu.item.csv.exception;

public class IllegalFileNameException extends CSVException {

	private static final long serialVersionUID = 1L;

	public IllegalFileNameException(String filename) {
		super(filename + " is an illegal filename");
	}

	public IllegalFileNameException() {
		super();
	}

}
