package com.doublefx.blockchain.example.chat;

import com.doublefx.blockchain.example.common.ChatUser;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public final class Message implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public static final String NEW_LINE = "\n";

	private final UUID     uuid;
	private final ChatUser from;
	private final String   content;
	private final byte[]   signature;

	public Message(ChatUser from, String content) {
		this.uuid    = UUID.randomUUID();
		this.from    = from;
		this.content = content;

		signature = from.sign(content);
	}

	public ChatUser getFrom() {
		return from;
	}

	public String getContent() {
		return content;
	}

	public byte[] getSignature() {
		return signature;
	}

	@Override
	public String toString() {
		return from.getName() + ": " + getContent();
	}

	@Override
	public boolean equals(Object otherMessage) {
		if (this == otherMessage) return true;
		if (ofNullable(otherMessage).isEmpty() || getClass() != otherMessage.getClass()) return false;

		final var message = (Message) otherMessage;

		if (!uuid.equals(message.uuid)) return false;
		if (!from.equals(message.from)) return false;
		if (!content.equals(message.content)) return false;

		return Arrays.equals(signature, message.signature);
	}

	@Override
	public int hashCode() {
		var result = uuid.hashCode();

		result = 31 * result + from.hashCode();
		result = 31 * result + content.hashCode();
		result = 31 * result + Arrays.hashCode(signature);

		return result;
	}
}
