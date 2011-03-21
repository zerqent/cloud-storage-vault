package no.ntnu.item.csv.exception;

public class DuplicateAliasException extends CSVException {

	public DuplicateAliasException(String alias) {
		super("Alias \"" + alias + "\" already exists");
	}

	public DuplicateAliasException() {
		super();
	}

	private static final long serialVersionUID = 1L;

}
