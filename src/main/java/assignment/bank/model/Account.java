package assignment.bank.model;

import java.math.BigDecimal;

import assignment.bank.exception.InsufficientFundsException;

public class Account {

	private String accountNumber;

	private volatile BigDecimal balance;

	private static final String INSUFFICIENT_FUNDS = "Insufficient funds, balance = %s";

	public Account(String accountNumber, BigDecimal initialDeposit) {
		this.accountNumber = accountNumber;
		if (initialDeposit == null) {
			initialDeposit = BigDecimal.valueOf(0);
		}
		this.balance = initialDeposit;
	}

	/**
	 * This method represents the deposit action on the account The amount is added
	 * to the balance of the account
	 * 
	 * @param amount
	 */
	public void deposit(BigDecimal amount) {
		/*
		 * This operation needs to be synchronized for thread safety so that while one
		 * thread is depositing the amount, no other thread can access the balance.
		 */
		synchronized (this) {
			doDeposit(amount);
		}
	}

	/**
	 * This method represents the withdraw function on the account The amount is
	 * deducted from the balance of the account.
	 * 
	 * @param amount
	 * @throws InsufficientFundsException
	 */
	public void withdraw(BigDecimal amount) throws InsufficientFundsException {
		/*
		 * This operation needs to be synchronized for thread safety so that while one
		 * thread is depositing the amount, no other thread can access the balance.
		 */
		synchronized (this) {
			doWithdraw(amount);
		}
	}

	/**
	 * This method represents the transfer action The amount from this account is
	 * transfered to "to" account.
	 * 
	 * @param to
	 * @param amount
	 * @throws InsufficientFundsException
	 */
	public void transfer(Account to, BigDecimal amount) throws InsufficientFundsException {
		/*
		 * Here we need to obtain lock on both the accounts(to and from). While the
		 * transfer operation is in progress, no other thread can access both the
		 * accounts balance.
		 */
		Object lock1 = Long.parseLong(accountNumber) < Long.parseLong(to.getAccountNumber()) ? this : to;
		Object lock2 = Long.parseLong(accountNumber) < Long.parseLong(to.getAccountNumber()) ? to : this;
		synchronized (lock1) {
			doWithdraw(amount);
			synchronized (lock2) {
				to.doDeposit(amount);
			}
		} 
	}

	private void doWithdraw(BigDecimal amount) throws InsufficientFundsException {
		if (balance.compareTo(amount) < 0) {
			throw new InsufficientFundsException(String.format(INSUFFICIENT_FUNDS, balance));
		}
		balance = balance.subtract(amount);
	}

	private void doDeposit(BigDecimal amount) {
		balance = balance.add(amount);
	}

	public double getBalance() {
		return balance.doubleValue();
	}

	public String getAccountNumber() {
		return accountNumber;
	}
}
