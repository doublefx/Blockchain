package com.doublefx.blockchain.example.virtualcoin.collector;

import com.doublefx.blockchain.example.common.AccountHolder;
import com.doublefx.blockchain.example.virtualcoin.Transaction;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

public class BalanceCollector {
	private static final long INITIAL_ACCOUNT_BALANCE = 100;

	private final AccountHolder accountHolder;

	private BigDecimal totalReceived = valueOf(INITIAL_ACCOUNT_BALANCE);
	private BigDecimal totalSent     = ZERO;

	public BalanceCollector(AccountHolder accountHolder) {
		this.accountHolder = accountHolder;
	}

	public void accept(Transaction transaction) {
		final var amount = transaction.amount();

		if (accountHolder.equals(transaction.from())) {
			totalSent = totalSent.add(amount);
		}
		if (accountHolder.equals(transaction.to())) {
			totalReceived = totalReceived.add(amount);
		}
	}

	public void combine(BalanceCollector other) {
		totalSent     = totalSent.add(other.totalSent);
		totalReceived = totalReceived.add(other.totalReceived);
	}

	public BigDecimal getBalance() {
		return totalReceived.subtract(totalSent);
	}
}
