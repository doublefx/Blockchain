package com.doublefx.blockchain.stage;

import com.doublefx.blockchain.example.virtualcoin.Transaction;
import com.doublefx.blockchain.example.virtualcoin.VirtualCoinNode;
import com.doublefx.blockchain.example.virtualcoin.VirtualCoinTransactionPool;

import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class Stage6 {
	private static final VirtualCoinTransactionPool transactionPool = VirtualCoinTransactionPool.getInstance();

	public static void run() {
		// Starts the transaction pool which is based on the blockchain (extends Blockchain),
		// clearing any saved history and creating the first block with no transactions.
		transactionPool.start();

		sendTransactions();
	}

	private static void sendTransactions() {
		final var sendMoney = (Consumer<Transaction>) transaction -> transaction.from().sendMoney(transaction);

		// VirtualCoinNode is a fake node holding a list of some generated local transactions.
		final var transactionList = VirtualCoinNode.transactionList;

		// Get and remove the first set of transactions from the head of the list.
		var transactions = transactionList.poll();

		while (ofNullable(transactions).isPresent()) {
			// By making the users to send each of their transactions in parallel but yet ordered,
			// that will add them to the transaction pool's pending transactions.
			transactions.stream()
			            .parallel()
			            .forEachOrdered(sendMoney);

			// Then process and print all the transaction pool's pending transactions
			// (mining 1 block from the blockchain point of view).
			transactionPool.processPendingTransactions();

			// Repeat until there are no more sets of transactions.
			transactions = transactionList.poll();
		}
	}
}
