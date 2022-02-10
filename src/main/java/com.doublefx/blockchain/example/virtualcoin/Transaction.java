package com.doublefx.blockchain.example.virtualcoin;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static java.util.Optional.ofNullable;

public final class Transaction implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private final AccountHolder from;
	private final AccountHolder to;
	private final BigDecimal    amount;

	private final byte[] signature;

	public Transaction(AccountHolder from, AccountHolder to, BigDecimal amount) {
		this.from   = from;
		this.to     = to;
		this.amount = amount;

		signature = from.sign(this.toString());
	}

	public AccountHolder from() {
		return from;
	}

	public AccountHolder to() {
		return to;
	}

	public BigDecimal amount() {
		return amount;
	}

	public byte[] getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		final var money = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

		return from.getName() + " sent " + money + " VC to " + to.getName();
	}

	@Override
	public boolean equals(Object otherTransaction) {
		if (otherTransaction == this) return true;
		if (ofNullable(otherTransaction).isEmpty() || otherTransaction.getClass() != this.getClass()) return false;

		final var that = (Transaction) otherTransaction;

		return Objects.equals(this.from, that.from) &&
		       Objects.equals(this.to, that.to) &&
		       Objects.equals(this.amount, that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to, amount);
	}
}
