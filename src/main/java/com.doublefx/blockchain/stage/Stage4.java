package com.doublefx.blockchain.stage;

import com.doublefx.blockchain.example.chat.Babbler;
import com.doublefx.blockchain.example.chat.ChatSystem;
import com.doublefx.blockchain.example.chat.Message;

import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class Stage4 {
	private static final ChatSystem chatSystem = ChatSystem.getInstance();

	public static void run() {
		// Starts the chat system which is based on the blockchain (extends Blockchain),
		// clearing any saved history and creating the first block with no message.
		chatSystem.start();

		sendUserMessages();
	}

	private static void sendUserMessages() {
		final var sendMessage = (Consumer<Message>) message -> message.getFrom().send(message);
		final var messageList = Babbler.messageList;

		// Get and remove the first set of messages from the head of the list.
		var messages = messageList.poll();

		while (ofNullable(messages).isPresent()) {
			// By making the users to chat each of their messages in parallel but yet ordered,
			// that will add those incoming messages to the chat system's message list.
			messages.stream()
			        .parallel()
			        .forEachOrdered(sendMessage);

			// Then process and print all the messages we just added to the chat system
			// (mining 1 block from the blockchain point of view).
			chatSystem.send();

			// Repeat until there are no more sets of messages.
			messages = messageList.poll();
		}
	}
}
