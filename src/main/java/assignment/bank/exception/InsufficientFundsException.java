package assignment.bank.exception;

public class InsufficientFundsException extends BankAccountException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InsufficientFundsException(String message) {
		super(message);
	}
}
