package com.doublefx.blockchain.example.chat;

import com.doublefx.blockchain.example.common.ChatUser;
import com.doublefx.blockchain.util.SynchronizedLinkedList;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class Babbler {
	public static final LinkedList<Set<Message>> messageList = new SynchronizedLinkedList<>();

	static {
		final var tom   = new ChatUser("Tom");
		final var sarah = new ChatUser("Sarah");
		final var nick  = new ChatUser("Nick");
		final var fred  = new ChatUser("Fred");

		final var messageSet1 = new LinkedHashSet<Message>();

		messageSet1.add(new Message(tom, "Hey, I'm first!"));

		final var messageSet2 = new LinkedHashSet<Message>();

		messageSet2.add(new Message(sarah, "It's not fair!"));
		messageSet2.add(new Message(sarah, "You always will be first because it is your blockchain!"));
		messageSet2.add(new Message(sarah, "Anyway, thank you for this amazing chat."));

		final var messageSet3 = new LinkedHashSet<Message>();

		messageSet3.add(new Message(tom, "You're welcome :)"));
		messageSet3.add(new Message(nick, "Hey Tom, nice chat"));

		final var messageSet4 = new LinkedHashSet<Message>();

		messageSet4.add(new Message(fred, "Well, I hope it's gonna work 😎"));
		messageSet4.add(new Message(tom, "me too!!"));
		messageSet4.add(new Message(sarah, "Yeah, let's hope ❤️"));
		messageSet4.add(new Message(nick, "Finger crossed 🤞"));

		messageList.add(messageSet1);
		messageList.add(messageSet2);
		messageList.add(messageSet3);
		messageList.add(messageSet4);
	}
}
