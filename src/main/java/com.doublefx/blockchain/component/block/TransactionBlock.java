package com.doublefx.blockchain.component.block;

import com.doublefx.blockchain.example.virtualcoin.AccountHolder;
import com.doublefx.blockchain.example.virtualcoin.Transaction;
import com.doublefx.blockchain.util.StringUtils;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static com.doublefx.blockchain.example.chat.Message.NEW_LINE;
import static com.doublefx.blockchain.example.virtualcoin.AccountHolder.getBlockchainUser;
import static java.math.BigDecimal.ZERO;
import static java.math.BigInteger.ONE;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class TransactionBlock extends AbstractBlock<Transaction> {
	@Serial
	private static final long serialVersionUID = 1L;

	private static final String NO_TRANSACTIONS   = "No transactions";
	private static final String BLOCK_DESCRIPTION =
		"""
		Block:
		Created by: %s
		%s gets %s VC
		Id: %s
		Timestamp: %s
		Magic number: %s
		Hash of the previous block:
		%s
		Hash of the block:
		%s
		Block data: %s
		Block was generating for %s seconds
		""";

	private final static Predicate<Transaction> isNotSystemUser =
		transaction -> !AccountHolder.isSystemUser(transaction.from());

	public static TransactionBlock root() {
		return new TransactionBlock(ONE, ROOT_INITIAL_HASH, Collections.emptyList());
	}

	public TransactionBlock(BigInteger id, String previousHash, List<Transaction> data) {
		super(id, previousHash, data);
	}

	@Override
	public String toString() {
		final var miner = getMiner().getName();

		return String.format(BLOCK_DESCRIPTION,
		                     miner,
		                     miner, getMinerReward(),
		                     getId(),
		                     getTimestamp(),
		                     getNonce(),
		                     getPreviousHash(),
		                     getHash(),
		                     formatData(getData()),
		                     getMiningDuration() / 1_000);
	}

	private BigDecimal getMinerReward() {
		final Predicate<Transaction> isReward = transaction -> getBlockchainUser().equals(transaction.from())
		                                                       && getMiner().equals(transaction.to());

		final var transactions = getData();
		return transactions.stream()
		                   .filter(isReward)
		                   .map(Transaction::amount)
		                   .findAny()
		                   .orElse(ZERO);
	}

	@Override
	protected String formatData(List<Transaction> transactions) {
		final var hasTransactions = ofNullable(transactions).isPresent() && !transactions.isEmpty();

		String formattedTransactions = NO_TRANSACTIONS;

		if (hasTransactions) {
			var data = transactions.stream()
			                       .filter(isNotSystemUser)
			                       .map(Transaction::toString)
			                       .collect(joining(NEW_LINE));

			if (StringUtils.isNotBlank(data)) {
				formattedTransactions = NEW_LINE + data;
			}
		}

		return formattedTransactions;
	}
}
