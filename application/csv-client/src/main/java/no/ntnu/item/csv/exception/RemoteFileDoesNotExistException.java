package no.ntnu.item.csv.exception;

public class RemoteFileDoesNotExistException extends CSVException {

	private static final long serialVersionUID = 1L;

	public RemoteFileDoesNotExistException(String file) {
		super("File \"" + file + "\" does not exist");
	}

	public RemoteFileDoesNotExistException() {
		super("The remote file does not exist");
	}
}
