package com.doublefx.blockchain.example.chat;

import com.doublefx.blockchain.component.block.AbstractBlock;

import java.io.Serial;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.doublefx.blockchain.example.chat.Message.NEW_LINE;
import static com.doublefx.blockchain.util.StringUtils.isNotBlank;
import static java.math.BigInteger.ONE;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class MessageBlock extends AbstractBlock<Message> {
	@Serial
	private static final long serialVersionUID = 1L;

	private static final String NO_MESSAGES = "no messages";

	public static MessageBlock root() {
		return new MessageBlock(ONE, ROOT_INITIAL_HASH, Collections.emptyList());
	}

	public MessageBlock(BigInteger id, String previousHash, List<Message> data) {
		super(id, previousHash, data);
	}

	@Override
	protected String formatData(List<Message> messages) {
		final var hasMessages = ofNullable(messages).isPresent() && !messages.isEmpty();

		var formattedMessages = NO_MESSAGES;

		if (hasMessages) {
			var data = messages.stream()
			                   .map(Message::toString)
			                   .collect(joining(NEW_LINE));

			if (isNotBlank(data)) {
				formattedMessages = NEW_LINE + data;
			}
		}

		return formattedMessages;
	}
}
