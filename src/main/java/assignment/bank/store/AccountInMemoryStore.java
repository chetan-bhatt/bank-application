package assignment.bank.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import assignment.bank.model.Account;

/**
 * This class represents the in memory store for the {@code Account} It uses
 * {@code HashMap} for storing the {@code Account} objects.
 *
 */
public class AccountInMemoryStore implements AccountStore {

	private static AccountInMemoryStore accountStore = new AccountInMemoryStore();

	private Map<String, Account> accountMap = new ConcurrentHashMap<>();

	private AccountInMemoryStore() {
	}

	public static AccountInMemoryStore getInstance() {
		return accountStore;
	}

	public void save(Account account) {
		accountMap.put(account.getAccountNumber(), account);
	}

	public Account retrieve(String accountNumber) {
		return accountMap.get(accountNumber);
	}
}
