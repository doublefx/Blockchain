package com.doublefx.blockchain.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static java.util.Optional.ofNullable;

public class StringUtils {

	/**
	 * Applies Sha256 to a string and returns a hash.
	 *
	 * @param input The String to hash.
	 * @return The hashed String.
	 */
	public static String toSha256(String input) {
		try {
			final var digest = MessageDigest.getInstance("SHA-256");

			/* Applies sha256 to our input */
			final var hash      = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			final var hexString = new StringBuilder();

			for (byte element : hash) {
				final var hex = Integer.toHexString(0xff & element);

				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isBlank(String string) {
		return ofNullable(string).isEmpty() || string.trim().isEmpty();
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}
}
