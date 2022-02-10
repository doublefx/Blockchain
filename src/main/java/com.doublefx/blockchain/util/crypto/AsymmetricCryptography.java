package com.doublefx.blockchain.util.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class AsymmetricCryptography {
	private static final int KEY_LENGTH = 1024;

	private static final String CANNOT_SIGN_THE_MESSAGE = "Cannot sign the message %s, reason %s";

	private static final String WORKING_DIR = "keys/";

	private static final String MESSAGE_NOT_SENT_BECAUSE_SIGNATURE_CANNOT_BE_VERIFIED =
		"""
		Message:\s
		%s
				
		not sent because signature cannot be verified, reason:
		 %s%n""";

	private static final String CANNOT_LOAD_MESSAGE_SIGNER = "Cannot load message signer for user %s, reason:\n %s";

	private final String userName;
	private final String relativePath;

	private MessageSigner     messageSigner;
	private SignatureVerifier signatureVerifier;

	public AsymmetricCryptography(String userName) {
		this.userName     = userName;
		this.relativePath = WORKING_DIR + userName;

		prepareSignature();
	}

	public byte[] sign(String data) {
		try {
			return messageSigner.sign(data);

		} catch (Exception exception) {
			throw new RuntimeException(String.format(CANNOT_SIGN_THE_MESSAGE, data, exception));
		}
	}

	public boolean verify(String data, byte[] signature) {
		var isVerified = false;

		try {
			isVerified = signatureVerifier.verify(data, signature);

		} catch (Exception exception) {
			System.out.printf(MESSAGE_NOT_SENT_BECAUSE_SIGNATURE_CANNOT_BE_VERIFIED, data, exception);
		}

		return isVerified;
	}

	private void prepareSignature() {
		try {
			loadMessageSigner();
			loadSignatureVerifier();

		} catch (Exception exception) {
			throw new RuntimeException(String.format(CANNOT_LOAD_MESSAGE_SIGNER, userName, exception));
		}
	}

	private void loadMessageSigner() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		final var privateKeyPath = Path.of(relativePath + ".privateKey");
		final var publicKeyPath  = Path.of(relativePath + ".publicKey");

		final var hasPrivateKeyFile = Files.exists(privateKeyPath);
		final var hasPublicKeyFile  = Files.exists(publicKeyPath);

		var generatesKeys = !(hasPrivateKeyFile && hasPublicKeyFile);

		if (!hasPrivateKeyFile && hasPublicKeyFile) {
			Files.delete(publicKeyPath);
		}

		if (!hasPublicKeyFile && hasPrivateKeyFile) {
			Files.delete(privateKeyPath);
		}

		if (generatesKeys) {
			final var keys = new GenerateKeys(KEY_LENGTH);

			keys.createKeys();
			keys.writeToFile(privateKeyPath, keys.getPrivateKey().getEncoded());
			keys.writeToFile(publicKeyPath, keys.getPublicKey().getEncoded());
		}

		messageSigner = new MessageSigner(privateKeyPath);
	}

	private void loadSignatureVerifier() throws Exception {
		final var publicKeyPath = Path.of(relativePath + ".publicKey");

		signatureVerifier = new SignatureVerifier(publicKeyPath.toAbsolutePath());
	}
}
