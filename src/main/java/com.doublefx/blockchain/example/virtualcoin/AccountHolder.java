package com.doublefx.blockchain.example.virtualcoin;

import com.doublefx.blockchain.example.common.User;

import java.io.Serial;

import static java.util.Optional.ofNullable;

public class AccountHolder extends User {
	@Serial
	private static final long serialVersionUID = 1L;

	public static synchronized AccountHolder getUserOrAddMiner(String userName) {
		var accountHolder = (AccountHolder) userMap.get(userName);

		if (ofNullable(accountHolder).isEmpty()) {
			accountHolder = new AccountHolder(userName, true);
		}

		return accountHolder;
	}

	public static AccountHolder getBlockchainUser() {
		return getUserOrAddMiner("VC_BLOCKCHAIN");
	}

	private final boolean isMiner;

	public static boolean isSystemUser(User user) {
		return getBlockchainUser().equals(user);
	}

	public AccountHolder(String userName, boolean isMiner) {
		super(userName);
		this.isMiner = isMiner;
	}

	public AccountHolder(String userName) {
		this(userName, false);
	}

	public boolean isMiner() {
		return isMiner;
	}

	public void sendMoney(Transaction transaction) {
		final var isVerified = signature.verify(transaction.toString(), transaction.getSignature());

		if (transaction.from().equals(this) && isVerified) {
			VirtualCoinTransactionPool.getInstance().add(transaction);

		} else {
			System.out.printf(MESSAGE_NOT_SENT_BECAUSE_WRONG_SIGNATURE, transaction);
		}
	}
}
