package no.ntnu.item.csv.exception;

public class IncorrectPasswordException extends CSVException {

	private static final long serialVersionUID = 1L;

	public IncorrectPasswordException() {
		super("Password is incorrect");
	}

	public IncorrectPasswordException(String password) {
		super(password + " is incorrect");
	}
}
