package no.ntnu.item.csv.exception;

public class ServerCommunicationException extends CSVException {

	private int code;

	private static final long serialVersionUID = 1L;

	public ServerCommunicationException(int statuscode) {
		super(parseHttpStatusCode(statuscode));
		this.code = statuscode;
	}

	public ServerCommunicationException(String msg) {
		super(msg);
	}

	public ServerCommunicationException() {
		this.code = -1;
	}

	public int getCode() {
		return this.code;
	}

	public static String parseHttpStatusCode(int statuscode) {
		String msg = "";
		switch (statuscode) {
		case 400:
			msg += "Bad request";
			break;
		case 500:
			msg += "Internal server error";
			break;
		case 600:
			msg += "FIXME";
			break;
		case 700:
			msg += "Access Denied";
			break;
		}

		return statuscode + ":" + msg;
	}

}
