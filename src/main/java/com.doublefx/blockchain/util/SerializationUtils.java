package com.doublefx.blockchain.util;

import java.io.*;

public class SerializationUtils {
	/**
	 * Serialize the given object to the file
	 */
	public static <T> void serialize(T obj, File file) throws IOException {
		final var fos = new FileOutputStream(file);
		final var bos = new BufferedOutputStream(fos);
		final var oos = new ObjectOutputStream(bos);

		oos.writeObject(obj);
		oos.close();
	}

	/**
	 * Deserialize to an object from the file
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(File file) throws IOException, ClassNotFoundException {
		final var fis = new FileInputStream(file);
		final var bis = new BufferedInputStream(fis);
		final var ois = new ObjectInputStream(bis);

		Object obj = ois.readObject();
		ois.close();

		return (T) obj;
	}

	/**
	 * Method to deep clone an object using in memory serialization.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepClone(T original) throws IOException, ClassNotFoundException {
		final var bos = new ByteArrayOutputStream();
		final var out = new ObjectOutputStream(bos);

		out.writeObject(original);

		final var bis = new ByteArrayInputStream(bos.toByteArray());
		final var in  = new ObjectInputStream(bis);

		return (T) in.readObject();
	}
}
