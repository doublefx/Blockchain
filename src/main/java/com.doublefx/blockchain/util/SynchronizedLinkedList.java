package com.doublefx.blockchain.util;

import java.util.LinkedList;

public class SynchronizedLinkedList<T> extends LinkedList<T> {
	@Override
	public synchronized boolean add(T t) {
		return super.add(t);
	}

	@Override
	public synchronized T poll() {
		return super.poll();
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}
}
