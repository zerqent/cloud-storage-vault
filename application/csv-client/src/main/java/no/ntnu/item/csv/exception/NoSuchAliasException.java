package no.ntnu.item.csv.exception;

public class NoSuchAliasException extends CSVException {

	private static final long serialVersionUID = 1L;

	public NoSuchAliasException(String alias) {
		super("Alias \"" + alias + "\" does not exist");
	}

	public NoSuchAliasException() {
		super("The requested alias does not exist");
	}

}
