package com.doublefx.blockchain.util.crypto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class GenerateKeys {
	private static final String RSA                                       = "RSA";
	private static final String CANNOT_CREATE_KEY_FILE_PARENT_DIRECTORIES = "Cannot create Key file parent directories";

	private final KeyPairGenerator keyGen;

	private PrivateKey privateKey;
	private PublicKey  publicKey;

	public GenerateKeys(int keyLength) throws NoSuchAlgorithmException {
		this.keyGen = KeyPairGenerator.getInstance(RSA);
		this.keyGen.initialize(keyLength);
	}

	public void createKeys() {
		final var pair = keyGen.generateKeyPair();

		privateKey = pair.getPrivate();
		publicKey  = pair.getPublic();
	}

	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	public void writeToFile(Path keyFilePath, byte[] key) throws IOException {
		final var parentDirectory = keyFilePath.toAbsolutePath().getParent().toFile();
		final var isSuccess       = parentDirectory.mkdirs();

		if (!(parentDirectory.exists() && parentDirectory.isDirectory())) {
			throw new IOException(CANNOT_CREATE_KEY_FILE_PARENT_DIRECTORIES);
		}

		final var keyFile = keyFilePath.toFile();

		final var fos = new FileOutputStream(keyFile);
		fos.write(key);
		fos.flush();
		fos.close();
	}
}
