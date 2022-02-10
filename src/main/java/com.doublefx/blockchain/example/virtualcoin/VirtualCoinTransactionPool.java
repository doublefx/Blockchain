package com.doublefx.blockchain.example.virtualcoin;

import com.doublefx.blockchain.component.Blockchain;
import com.doublefx.blockchain.component.block.AbstractBlock;
import com.doublefx.blockchain.component.block.Block;
import com.doublefx.blockchain.component.block.BlockWithDifficultyInfo;
import com.doublefx.blockchain.component.block.TransactionBlock;
import com.doublefx.blockchain.component.mining.MiningService;
import com.doublefx.blockchain.example.common.User;
import com.doublefx.blockchain.example.virtualcoin.collector.BalanceCollector;
import com.doublefx.blockchain.util.SynchronizedLinkedList;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.math.BigInteger.ONE;
import static java.util.Optional.ofNullable;

public final class VirtualCoinTransactionPool extends Blockchain {
	public static final int MINING_REWARD = 100;

	private static final String TRYING_TO_GET_THE_LATEST_BLOCK_OF_AN_EMPTY_BLOCKCHAIN =
		"Trying to get the latest block of an empty blockchain.";

	private static VirtualCoinTransactionPool INSTANCE;

	public static VirtualCoinTransactionPool getInstance() {
		return ofNullable(INSTANCE).isEmpty()
		       ? INSTANCE = new VirtualCoinTransactionPool()
		       : INSTANCE;
	}

	private final SynchronizedLinkedList<Transaction> pendingTransactions;
	private final MiningService                       miningService;

	private VirtualCoinTransactionPool() {
		super();

		this.pendingTransactions = new SynchronizedLinkedList<>();
		this.miningService       = MiningService.getInstance();
	}

	public void start() {
		clear();
		addRoot();
	}

	private void addRoot() {
		final var block = BlockWithDifficultyInfo.of(TransactionBlock.root());

		mineBlock(block);
	}

	public void add(Transaction transaction) {
		final var from       = transaction.from();
		final var isMePaying = AccountHolder.getBlockchainUser().equals(from);
		final var canAfford  = isMePaying || canAfford(transaction.from(), transaction.amount());

		if (canAfford) {
			pendingTransactions.add(transaction);
		}
	}

	public void processPendingTransactions() {
		if (!pendingTransactions.isEmpty()) {
			if (getLatestBlock().isPresent()) {

				final var transactionsToProcess = new LinkedList<>(pendingTransactions);
				pendingTransactions.clear();

				final var latestBlock = getLatestBlock().get();

				final var block = new TransactionBlock(latestBlock.getId().add(ONE),
				                                       latestBlock.getHash(),
				                                       transactionsToProcess);

				final var blockInfo = BlockWithDifficultyInfo.of(block);

				// Blocking until one of miners has successfully mined the block.
				mineBlock(blockInfo);

			} else {
				throw new IndexOutOfBoundsException(TRYING_TO_GET_THE_LATEST_BLOCK_OF_AN_EMPTY_BLOCKCHAIN);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void mineBlock(Block<List<Transaction>> block) {
		final BiFunction<User, Block<?>, Block<?>> rewardSupplier = (miner, originalBlock) -> {
			final var reward = new Transaction(AccountHolder.getBlockchainUser(),
			                                   (AccountHolder) miner,
			                                   BigDecimal.valueOf(MINING_REWARD));

			final var transactions =
				new LinkedList<Transaction>((Collection<? extends Transaction>) originalBlock.getData());

			transactions.add(reward);

			final var candidateBlock = new TransactionBlock(originalBlock.getId(),
			                                                isRoot()
			                                                ? AbstractBlock.ROOT_INITIAL_HASH
			                                                : originalBlock.getPreviousHash(),
			                                                transactions);

			return BlockWithDifficultyInfo.of(candidateBlock);
		};

		try {
			miningService.process(block,
			                      AccountHolder::getUserOrAddMiner,
			                      rewardSupplier,
			                      getProofOfWorkDifficulty(),
			                      this::add);

			print((Block<List<Transaction>>) getLatestBlock().orElseThrow());

		} catch (ExecutionException | InterruptedException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void print(Block<List<Transaction>> block) {
		System.out.println(block);
	}

	boolean canAfford(AccountHolder from, BigDecimal amountToPay) {
		final Predicate<Transaction> isMatchingAccountHolder = trans -> trans.from().equals(from);

		final var balance = getBalance(from);
		final var pendingPayments = pendingTransactions.stream()
		                                               .filter(isMatchingAccountHolder)
		                                               .map(Transaction::amount)
		                                               .reduce(ZERO, BigDecimal::add);

		final var totalBalance = balance.subtract(pendingPayments).subtract(amountToPay);

		return totalBalance.compareTo(ZERO) >= 0;
	}

	@SuppressWarnings("unchecked")
	BigDecimal getBalance(AccountHolder accountHolder) {

		final Function<Block<?>, Stream<? extends Transaction>> blocksToTransactionStream =
			block -> ((List<Transaction>) block.getData()).stream();

		final var ledgerStream = getBlocks().stream()
		                                    .parallel()
		                                    .flatMap(blocksToTransactionStream);

		final var totalBalance = ledgerStream.collect(() -> new BalanceCollector(accountHolder),
		                                              BalanceCollector::accept,
		                                              BalanceCollector::combine);


		return totalBalance.getBalance();
	}
}
