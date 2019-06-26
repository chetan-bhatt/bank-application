package assignment.bank.util;

import java.util.concurrent.atomic.AtomicLong;

public class AccountNumberGenerator {
	private static AccountNumberGenerator instance = new AccountNumberGenerator();

	private AtomicLong accountNumber = new AtomicLong(0);

	private AccountNumberGenerator() {
	}

	public static AccountNumberGenerator getInstance() {
		return instance;
	}

	public long getNextAccountNumber() {
		return accountNumber.incrementAndGet();
	}
}
