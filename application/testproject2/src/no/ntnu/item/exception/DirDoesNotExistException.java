package no.ntnu.item.exception;

public class DirDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1L;

	public DirDoesNotExistException(String e) {
		super(e);
	}
}
