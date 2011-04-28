package no.ntnu.item.csv.exception;

public class CSVException extends Exception {

	private static final long serialVersionUID = 1L;

	public CSVException(String s) {
		super(s);
	}

	public CSVException() {
		super("An error occured in the application");
	}

	public CSVException(Exception e) {
		super(e);
	}

}
