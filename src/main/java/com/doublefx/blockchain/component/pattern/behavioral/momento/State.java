package com.doublefx.blockchain.component.pattern.behavioral.momento;

public interface State<T> {
	T getState();

	void setState(T state);
}
