package com.doublefx.blockchain.component;

import com.doublefx.blockchain.component.block.Block;
import com.doublefx.blockchain.component.block.BlockWithDifficultyInfo;
import com.doublefx.blockchain.component.pattern.behavioral.momento.State;
import com.doublefx.blockchain.component.pattern.behavioral.momento.StateManager;
import com.doublefx.blockchain.component.pattern.behavioral.momento.StateValidator;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.math.BigInteger.ONE;
import static java.util.Optional.*;

public class Blockchain implements State<Blockchain.State> {
	private static final String N_WAS_INCREASED_TO = "N was increased to %s\n";
	private static final String N_WAS_DECREASED_BY = "N was decreased by %s\n";
	private static final String N_STAYS_THE_SAME   = "N stays the same\n";

	public static final Path blockchainPath = Path.of("blockchain.bin");

	private static final int INITIAL_DIFFICULTY  = 0;
	private static final int MAX_MINING_DURATION = 3000; // in milliseconds

	private static Blockchain INSTANCE;

	@SuppressWarnings("unused")
	public static Blockchain getInstance() {
		return ofNullable(INSTANCE).isEmpty()
		       ? INSTANCE = new Blockchain()
		       : INSTANCE;
	}

	private final StateManager<Blockchain, Blockchain.State> stateManager;

	private LinkedList<Block<?>> blocks;
	private int                  proofOfWorkDifficulty;

	protected Blockchain() {
		blocks                = new LinkedList<>();
		proofOfWorkDifficulty = INITIAL_DIFFICULTY;

		stateManager = new StateManager<>(this, blockchainPath, false);
	}

	protected void clear() {
		blocks.clear();
	}

	public synchronized void add(Block<?> block) {
		if (isValid(block)) {
			blocks.add(block);
			stateManager.save();

			final var blockchainHelper = new BlockchainHelper();
			proofOfWorkDifficulty = blockchainHelper.computeNextDifficulty(block, false);
		}
	}

	public int getProofOfWorkDifficulty() {
		return proofOfWorkDifficulty;
	}

	public boolean isEmpty() {
		return getBlocks().isEmpty();
	}

	protected boolean isRoot() {
		return isEmpty();
	}

	public int size() {
		return blocks.size();
	}

	public Optional<Block<?>> getLatestBlock() {
		return isEmpty()
		       ? empty()
		       : of(blocks.get(size() - 1));
	}

	public List<Block<?>> getBlocks() {
		return new LinkedList<>(blocks);
	}

	public State getState() {
		return new State(getBlocks());
	}

	public void setState(State state) {
		blocks = (LinkedList<Block<?>>) state.blocks;
	}

	private boolean isValid(Block<?> block) {
		final var latestBlock = getLatestBlock();

		return block.isProven()
		       && block.getProofOfWorkDifficulty() == proofOfWorkDifficulty
		       && ofNullable(block.getHash()).isPresent()
		       && block.getHash().startsWith("0".repeat(proofOfWorkDifficulty))
		       && (isRoot() || (latestBlock.isPresent()
		                        && block.getTimestamp() > latestBlock.get().getTimestamp()
		                        && block.getId().equals(latestBlock.get().getId().add(ONE))
		                        && block.getPreviousHash().equals(latestBlock.get().getHash())));
	}

	private class BlockchainHelper {

		public int computeNextDifficulty(Block<?> block, boolean keepAsIs) {
			final var currentDifficulty = block.getProofOfWorkDifficulty();

			if (keepAsIs) {
				// Fix for stage 6 running within 15s
				setNextDifficultyInfo(block, currentDifficulty, currentDifficulty);

				return currentDifficulty;

			} else {
				var nextDifficulty = currentDifficulty + (isRoot()
				                                          ? 1
				                                          : Long.compare(MAX_MINING_DURATION,
				                                                         block.getMiningDuration()));

				if (nextDifficulty < INITIAL_DIFFICULTY) {
					nextDifficulty = INITIAL_DIFFICULTY;
				}

				setNextDifficultyInfo(block, currentDifficulty, nextDifficulty);

				return nextDifficulty;
			}
		}

		private void setNextDifficultyInfo(Block<?> block, int currentDifficulty, int nextDifficulty) {
			if (block instanceof final BlockWithDifficultyInfo blockInfo) {

				if (isRoot() || nextDifficulty > currentDifficulty) {
					blockInfo.setNextBlockDifficultyInfo(String.format(N_WAS_INCREASED_TO, nextDifficulty));

				} else if (nextDifficulty < currentDifficulty) {
					blockInfo.setNextBlockDifficultyInfo(String.format(N_WAS_DECREASED_BY,
					                                                   Math.abs(nextDifficulty - currentDifficulty)));
				} else {
					blockInfo.setNextBlockDifficultyInfo(N_STAYS_THE_SAME);
				}
			}
		}
	}

	public record State(List<Block<?>> blocks) implements StateValidator, Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		public boolean isValid() {
			boolean isValid = true;

			for (int i = 0; i < blocks.size(); i++) {
				final var isRoot = i == 0;
				final var block  = blocks.get(i);

				final var latestBlock = isRoot
				                        ? null
				                        : blocks.get(i - 1);


				if (!(block.isProven()
				      && ofNullable(block.getHash()).isPresent()
				      && isRoot || (ofNullable(latestBlock).isPresent()
				                    && block.getTimestamp() > latestBlock.getTimestamp()
				                    && block.getId().equals(latestBlock.getId().add(ONE))
				                    && block.getPreviousHash().equals(latestBlock.getHash())))) {
					isValid = false;
					break;
				}
			}

			return isValid;
		}

	}
}
