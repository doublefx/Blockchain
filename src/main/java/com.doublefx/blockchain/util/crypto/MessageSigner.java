package com.doublefx.blockchain.util.crypto;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static java.nio.file.Files.readAllBytes;

public class MessageSigner {
	private static final String RSA = "SHA1withRSA";

	private final PrivateKey privateKey;

	public MessageSigner(Path privateKeyPath) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		final var keyBytes = readAllBytes(privateKeyPath);
		final var spec     = new PKCS8EncodedKeySpec(keyBytes);
		final var kf       = KeyFactory.getInstance("RSA");

		privateKey = kf.generatePrivate(spec);
	}

	public byte[] sign(String data) throws Exception {
		final var rsa = Signature.getInstance(RSA);

		rsa.initSign(privateKey);
		rsa.update(data.getBytes());

		return rsa.sign();
	}
}
