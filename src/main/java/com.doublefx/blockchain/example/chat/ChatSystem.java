package com.doublefx.blockchain.example.chat;

import com.doublefx.blockchain.example.common.User;
import com.doublefx.blockchain.component.Blockchain;
import com.doublefx.blockchain.component.block.Block;
import com.doublefx.blockchain.component.block.BlockWithDifficultyInfo;
import com.doublefx.blockchain.component.block.MessageBlock;
import com.doublefx.blockchain.component.mining.MiningService;
import com.doublefx.blockchain.example.virtualcoin.AccountHolder;
import com.doublefx.blockchain.util.SynchronizedLinkedList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import static java.math.BigInteger.ONE;
import static java.util.Optional.ofNullable;

public final class ChatSystem extends Blockchain {
	private static final String TRYING_TO_GET_THE_LATEST_BLOCK_OF_AN_EMPTY_BLOCKCHAIN =
		"Trying to get the latest block of an empty blockchain.";

	private static ChatSystem INSTANCE;

	public static ChatSystem getInstance() {
		return ofNullable(INSTANCE).isEmpty()
		       ? INSTANCE = new ChatSystem()
		       : INSTANCE;
	}

	private final SynchronizedLinkedList<Message> messages;
	private final MiningService                   miningService;

	private ChatSystem() {
		super();

		this.messages      = new SynchronizedLinkedList<>();
		this.miningService = MiningService.getInstance();
	}

	public void start() {
		clear();
		addRoot();
	}

	private void addRoot() {
		final var block = BlockWithDifficultyInfo.of(MessageBlock.root());

		processBlock(block);
	}

	public void add(Message message) {
		messages.add(message);
	}

	public void send() {
		if (!messages.isEmpty()) {
			if (getLatestBlock().isPresent()) {
				final var latestBlock = getLatestBlock().get();

				final var block     = new MessageBlock(latestBlock.getId().add(ONE), latestBlock.getHash(), messages);
				final var blockInfo = BlockWithDifficultyInfo.of(block);

				processBlock(blockInfo);

				messages.clear();

			} else {
				throw new IndexOutOfBoundsException(TRYING_TO_GET_THE_LATEST_BLOCK_OF_AN_EMPTY_BLOCKCHAIN);
			}
		}
	}

	private void processBlock(Block<List<Message>> block) {
		final BiFunction<User, Block<?>, Block<?>> rewardSupplier = (miner, originalBlock) -> originalBlock;

		try {
			miningService.process(block,
			                      AccountHolder::getUserOrAddMiner,
			                      rewardSupplier,
			                      getProofOfWorkDifficulty(),
			                      this::add);
			print(block);

		} catch (ExecutionException | InterruptedException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void print(Block<List<Message>> block) {
		System.out.println(block);
	}
}
