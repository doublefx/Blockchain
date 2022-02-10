package com.doublefx.blockchain.example.common;

import com.doublefx.blockchain.util.crypto.AsymmetricCryptography;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	protected static final Map<String, User> userMap = new HashMap<>();

	protected static final String MESSAGE_NOT_SENT_BECAUSE_WRONG_SIGNATURE =
		"""
		Message:\s
		%s
				
		not sent because of a wrong signature.%n""";

	protected final           UUID                   uuid;
	protected final           String                 userName;
	protected final transient AsymmetricCryptography signature;

	public User(String userName) {
		this.uuid     = UUID.randomUUID();
		this.userName = userName;

		signature = new AsymmetricCryptography(userName);

		userMap.put(userName, this);
	}

	public String getName() {
		return userName;
	}

	public byte[] sign(String data) {
		return signature.sign(data);
	}

	@Override
	public String toString() {
		return "ChatUser[" +
		       "uuid=" + uuid + ", " +
		       "name=" + userName + ']';
	}

	@Override
	public boolean equals(Object otherUser) {
		if (this == otherUser) return true;
		if (!(otherUser instanceof User user)) return false;
		if (!uuid.equals(user.uuid)) return false;

		return userName.equals(user.userName);
	}

	@Override
	public int hashCode() {
		var result = uuid.hashCode();

		result = 31 * result + userName.hashCode();

		return result;
	}
}
