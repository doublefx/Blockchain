package com.doublefx.blockchain.component.mining;

import com.doublefx.blockchain.component.block.Block;
import com.doublefx.blockchain.example.common.User;

import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.doublefx.blockchain.component.mining.MiningCommand.NUMBER_OF_MINERS;
import static java.util.Optional.ofNullable;

public final class MiningService {
	private static MiningService INSTANCE;

	public static MiningService getInstance() {
		return ofNullable(INSTANCE).isEmpty()
		       ? INSTANCE = new MiningService()
		       : INSTANCE;
	}

	public void process(Block<?> block,
	                    Function<String, User> minerSupplier,
	                    BiFunction<User, Block<?>, Block<?>> rewardSupplier,
	                    int difficulty,
	                    Consumer<Block<?>> blockConsumer) throws ExecutionException, InterruptedException {
		final var miningCommand = new MiningCommand();

		IntStream
			.rangeClosed(1, NUMBER_OF_MINERS)
			.forEach(minerId -> miningCommand.addTask(new MiningTask(minerSupplier.apply("miner" + minerId),
			                                                         block,
			                                                         rewardSupplier,
			                                                         difficulty,
			                                                         blockConsumer)));

		miningCommand.execute();
	}
}
