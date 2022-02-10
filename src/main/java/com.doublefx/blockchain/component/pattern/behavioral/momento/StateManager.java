package com.doublefx.blockchain.component.pattern.behavioral.momento;

import com.doublefx.blockchain.util.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

public class StateManager<S extends State<V>, V extends StateValidator> {
	private final S        state;
	private final File     file;
	private final boolean  log;
	private final Deque<V> history;

	public StateManager(S state, Path statesFilePath, boolean log) {
		this.state = state;
		this.file  = statesFilePath.toFile();
		this.log   = log;
		history    = new ArrayDeque<>();

		loadStateFromFile();
	}

	public void save() {
		history.push(state.getState());
		saveStateToFile();
	}

	public void undo() {
		if (!history.isEmpty()) {
			state.setState(history.pop());
		}
	}

	private void loadStateFromFile() {
		var state = this.state.getState();

		try {
			V loadedState = SerializationUtils.deserialize(file);

			if (loadedState.isValid()) {
				state = loadedState;
			}

		} catch (IOException | ClassNotFoundException e) {
			if (log) {
				System.out.println("No valid blockchain found: new blockchain created.\n");
			}
		}

		history.push(state);
		this.state.setState(state);
	}

	private void saveStateToFile() {
		try {
			SerializationUtils.serialize(state.getState(), file);

		} catch (IOException e) {
			if (log) {
				System.out.println("Unable to save current state: reverted to previous state.\n");
			}

			undo();
		}
	}
}
