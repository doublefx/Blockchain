package com.doublefx.blockchain.util.crypto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class SignatureVerifier {
	private static final String RSA = "SHA1withRSA";

	private final PublicKey publicKey;

	public SignatureVerifier(Path publicKeyPath) throws Exception {
		publicKey = getPublic(publicKeyPath);
	}

	public boolean verify(String data, byte[] signature) throws Exception {
		final var sig = Signature.getInstance(RSA);

		sig.initVerify(publicKey);
		sig.update(data.getBytes());

		return sig.verify(signature);
	}

	private PublicKey getPublic(Path publicKeyPath) throws Exception {
		final var keyBytes = Files.readAllBytes(publicKeyPath);
		final var spec     = new X509EncodedKeySpec(keyBytes);
		final var kf       = KeyFactory.getInstance("RSA");

		return kf.generatePublic(spec);
	}
}
