package com.doublefx.blockchain.component.pattern.behavioral;

import java.util.concurrent.ExecutionException;

public interface Command {
	void execute() throws ExecutionException, InterruptedException;
}
