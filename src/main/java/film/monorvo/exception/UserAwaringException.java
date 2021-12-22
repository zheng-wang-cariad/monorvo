package film.monorvo.exception;

public class UserAwaringException extends Exception {
	private static final long serialVersionUID = -8978937700459040182L;
	
	public UserAwaringException(Exception e) {
		super(e);
	}

	public UserAwaringException(String string) {
		super(string);
	}
}
