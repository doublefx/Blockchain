package com.doublefx.blockchain.component.block;

import com.doublefx.blockchain.example.chat.Message;
import com.doublefx.blockchain.util.StringUtils;

import java.io.Serial;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static com.doublefx.blockchain.example.chat.Message.NEW_LINE;
import static java.math.BigInteger.ONE;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class MessageBlock extends AbstractBlock<Message> {
	@Serial
	private static final long serialVersionUID = 1L;

	public static MessageBlock root() {
		return new MessageBlock(ONE, ROOT_INITIAL_HASH, Collections.emptyList());
	}

	public MessageBlock(BigInteger id, String previousHash, List<Message> data) {
		super(id, previousHash, data);
	}

	@Override
	protected String formatData(List<Message> messages) {
		final var hasMessages = ofNullable(messages).isPresent() && !messages.isEmpty();

		String formattedMessages = "no messages";

		if (hasMessages) {
			var data = messages.stream()
			                   .map(Message::toString)
			                   .collect(joining(NEW_LINE));

			if (StringUtils.isNotBlank(data)) {
				formattedMessages = NEW_LINE + data;
			}
		}

		return formattedMessages;
	}
}
