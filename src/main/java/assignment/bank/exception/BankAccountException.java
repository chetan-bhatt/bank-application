package assignment.bank.exception;

public class BankAccountException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BankAccountException(Throwable e) {
		super(e);
	}

	public BankAccountException(String message) {
		super(message);
	}

}
