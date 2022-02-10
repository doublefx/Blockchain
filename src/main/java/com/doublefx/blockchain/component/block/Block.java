package com.doublefx.blockchain.component.block;

import com.doublefx.blockchain.component.mining.MiningTask;
import com.doublefx.blockchain.example.common.User;

import java.io.Serializable;
import java.math.BigInteger;

public interface Block<T> extends Serializable {
	void setProofOfWork(MiningTask.ProofOfWork proofOfWork);

	boolean isProven();

	BigInteger getId();

	long getTimestamp();

	String getPreviousHash();

	long getNonce();

	int getProofOfWorkDifficulty();

	long getMiningDuration();

	String getHash();

	User getMiner();

	T getData();
}
