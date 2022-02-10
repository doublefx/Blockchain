package com.doublefx.blockchain.example.virtualcoin;

import com.doublefx.blockchain.example.virtualcoin.collector.BalanceCollector;
import com.doublefx.blockchain.util.SynchronizedLinkedList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.math.BigDecimal.valueOf;

public class VirtualCoinNode {
	public static final LinkedList<Set<Transaction>> transactionList = new SynchronizedLinkedList<>();

	private static final Random       random = new Random();
	private static final List<String> users  = List.of("Tom",
	                                                   "Sarah",
	                                                   "Nick",
	                                                   "Fred",
	                                                   "Charlotte",
	                                                   "Gio",
	                                                   "Babbz",
	                                                   "Martina",
	                                                   "ShoesShop",
	                                                   "FastFood",
	                                                   "CarShop");

	static {
		users.forEach(AccountHolder::new);

		do {
			transactionList.add(generateTransactions());
		} while (transactionList.size() < 14);
	}

	private static Set<Transaction> generateTransactions() {
		final var transactions = new LinkedHashSet<Transaction>();

		IntStream.rangeClosed(1, random.nextInt(1, 5))
		         .forEach(idx -> {
			         final var fromIdx = random.nextInt(users.size());
			         final var toIdx   = random.nextInt(users.size());

			         final var from   = AccountHolder.getUserOrAddMiner(users.get(fromIdx));
			         final var to     = AccountHolder.getUserOrAddMiner(users.get(toIdx));
			         final var amount = getRandomAmount(from);

			         transactions.add(new Transaction(from, to, amount));
		         });

		return transactions;
	}

	private static BigDecimal getRandomAmount(AccountHolder user) {
		final var max          = getBalance(user);
		final var randomAmount = valueOf(random.nextDouble(0.01, max.doubleValue() / 2));

		return randomAmount.setScale(2, RoundingMode.HALF_UP);
	}

	private static BigDecimal getBalance(AccountHolder accountHolder) {
		final var ledgerStream = transactionList.stream()
		                                        .flatMap(Collection::stream);

		final var totalBalance = ledgerStream.collect(() -> new BalanceCollector(accountHolder),
		                                              BalanceCollector::accept,
		                                              BalanceCollector::combine);


		return totalBalance.getBalance();
	}
}
