package com.doublefx.blockchain.component.mining;

import com.doublefx.blockchain.component.pattern.behavioral.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MiningCommand implements Command {
	public static final int NUMBER_OF_MINERS;

	static {
		final var random = new Random();
		final var cores  = Runtime.getRuntime().availableProcessors();

		NUMBER_OF_MINERS = random.nextInt(1, cores);
	}

	private final List<Callable<Void>> miningTasks = new ArrayList<>();

	public void addTask(MiningTask miningTask) {
		miningTasks.add(miningTask);
	}

	@Override
	public void execute() throws ExecutionException, InterruptedException {
		final var executor = Executors.newFixedThreadPool(NUMBER_OF_MINERS);

		executor.invokeAny(miningTasks);
		executor.shutdown();
	}
}
