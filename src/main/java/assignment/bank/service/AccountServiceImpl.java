package assignment.bank.service;

import java.math.BigDecimal;

import assignment.bank.exception.BankAccountException;
import assignment.bank.model.Account;
import assignment.bank.store.AccountInMemoryStore;
import assignment.bank.store.AccountStore;
import assignment.bank.util.AccountNumberGenerator;

/**
 * This class represents the service, responsible for all the account
 * operations.
 *
 */
public class AccountServiceImpl implements AccountService {

	private AccountStore accountStore;
	
	
	//TODO: needs to externalize.
	private static final String ACCOUNT_DOES_NOT_EXIST = "Account with account number %s does not exist.";

	private static final String INVALID_AMOUNT = "Invalid amount: %s";
	
	private static final String INVALID_ACCOUNT_NUMBER = "Invalid account number: %s";

	public AccountServiceImpl() {
		accountStore = AccountInMemoryStore.getInstance();
	}

	public Account create(double initialDeposit) throws BankAccountException {
		String accountNumber = String.valueOf(AccountNumberGenerator.getInstance().getNextAccountNumber());
		Account account = new Account(accountNumber, BigDecimal.valueOf(initialDeposit));
		accountStore.save(account);

		return account;
	}

	public double deposit(String accountNumber, double amountToDeposit) throws BankAccountException {		
		validateAmount(amountToDeposit);
		
		Account account = get(accountNumber);
		account.deposit(BigDecimal.valueOf(amountToDeposit));
		return account.getBalance();
	}

	public Account get(String accountNumber) throws BankAccountException {
		validateAccountNumber(accountNumber);
		Account account = accountStore.retrieve(accountNumber);
		if (account == null) {
			throw new BankAccountException(String.format(ACCOUNT_DOES_NOT_EXIST, accountNumber));
		}
		return account;
	}

	public double transfer(String fromAccntNumber, String toAccntNumber, double amount) throws BankAccountException {
		validateAmount(amount);
		
		Account fromAccount = get(fromAccntNumber);
		Account toAccount = get(toAccntNumber);

		fromAccount.transfer(toAccount, BigDecimal.valueOf(amount));
		return fromAccount.getBalance();
	}
	
	private void validateAmount(double amount) throws BankAccountException {
		if (amount <= 0) {
			throw new BankAccountException(String.format(INVALID_AMOUNT, amount));
		}
	}
	
	private void validateAccountNumber(String accountNumber) throws BankAccountException {
		if(accountNumber == null || accountNumber.isEmpty()) {
			throw new BankAccountException(String.format(INVALID_ACCOUNT_NUMBER, accountNumber));
		}
	}
}