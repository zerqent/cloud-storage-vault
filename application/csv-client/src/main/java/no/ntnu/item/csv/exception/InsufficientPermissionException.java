package no.ntnu.item.csv.exception;

public class InsufficientPermissionException extends CSVException {

	private static final long serialVersionUID = 1L;

	public InsufficientPermissionException(String folderalias) {
		super("You do not have access to " + folderalias);
	}

	public InsufficientPermissionException() {
		super("Access denied");
	}

}
