package com.doublefx.blockchain.component.mining;

import com.doublefx.blockchain.component.block.Block;
import com.doublefx.blockchain.example.common.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.doublefx.blockchain.util.StringUtils.toSha256;

public record MiningTask(User miner,
                         Block<?> block,
                         BiFunction<User, Block<?>, Block<?>> rewardSupplier,
                         int difficulty,
                         Consumer<Block<?>> blockchain) implements Callable<Void> {

	@Override
	public Void call() {
		doMining();

		return null;
	}

	private void doMining() {
		final var candidateBlock = rewardSupplier.apply(miner, block);
		final var proofOfWork    = ProofOfWork.mine(miner, candidateBlock, difficulty);

		candidateBlock.setProofOfWork(proofOfWork);
		blockchain.accept(candidateBlock);
	}

	public record ProofOfWork(User miner, int difficulty, int nonce, String hash) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		private static final Random random = new Random();

		public static ProofOfWork mine(User miner, Block<?> candidateBlock, int difficulty) {
			final var proofTemplate = "0".repeat(difficulty);

			int    nonce;
			String hash;

			do {
				if (Thread.currentThread().isInterrupted()) {
					return null;
				}

				nonce = Math.abs(random.nextInt());

				final var data = candidateBlock.getId().intValue()
				                 + candidateBlock.getTimestamp()
				                 + candidateBlock.getPreviousHash()
				                 + candidateBlock.getData()
				                 + nonce;

				hash = toSha256(data);

			} while (!hash.startsWith(proofTemplate));

			return new ProofOfWork(miner, difficulty, nonce, hash);
		}
	}
}
