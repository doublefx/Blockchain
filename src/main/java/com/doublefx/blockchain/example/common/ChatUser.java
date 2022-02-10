package com.doublefx.blockchain.example.common;

import com.doublefx.blockchain.example.chat.ChatSystem;
import com.doublefx.blockchain.example.chat.Message;

import java.io.Serial;

import static java.util.Optional.ofNullable;

public final class ChatUser extends User {
	@Serial
	private static final long serialVersionUID = 1L;

	public static ChatUser getOrAddUserByName(String userName) {
		var chatUser = (ChatUser) userMap.get(userName);

		if (ofNullable(chatUser).isPresent()) {
			chatUser = new ChatUser(userName);
		}

		return chatUser;
	}

	public static ChatUser getBlockchainUser() {
		return getOrAddUserByName("CHAT_SYSTEM");
	}

	public ChatUser(String userName) {
		super(userName);
	}

	public void send(Message message) {
		final var isVerified = signature.verify(message.getContent(), message.getSignature());

		if (message.getFrom().equals(this) && isVerified) {
			ChatSystem.getInstance().add(message);

		} else {
			System.out.printf(MESSAGE_NOT_SENT_BECAUSE_WRONG_SIGNATURE, message);
		}
	}
}
