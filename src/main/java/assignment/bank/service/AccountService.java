package assignment.bank.service;

import assignment.bank.exception.BankAccountException;
import assignment.bank.model.Account;

public interface AccountService {

	public Account create(double initialDeposit) throws BankAccountException;
	
	public double deposit(String accountNumber, double amountToDeposit) throws BankAccountException;

	public Account get(String accountNumber) throws BankAccountException;

	public double transfer(String fromAccntNumber, String toAccntNumber, double amount) throws BankAccountException;

}
