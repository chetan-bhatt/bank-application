package assignment.bank.store;

import assignment.bank.model.Account;

public interface AccountStore {

	public void save(Account account);

	public Account retrieve(String accountNumber);
}
