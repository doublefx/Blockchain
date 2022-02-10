package com.doublefx.blockchain.component.block;

import com.doublefx.blockchain.example.common.User;
import com.doublefx.blockchain.component.mining.MiningTask;

import java.io.Serial;
import java.math.BigInteger;

public class BlockDecorator<T> implements Block<T> {
	@Serial
	private static final long serialVersionUID = 1L;

	protected final Block<T> block;

	protected BlockDecorator(Block<T> block) {
		this.block = block;
	}

	@Override
	public void setProofOfWork(MiningTask.ProofOfWork proofOfWork) {
		block.setProofOfWork(proofOfWork);
	}

	@Override
	public boolean isProven() {
		return block.isProven();
	}

	@Override
	public BigInteger getId() {
		return block.getId();
	}

	@Override
	public long getTimestamp() {
		return block.getTimestamp();
	}

	@Override
	public long getNonce() {
		return block.getNonce();
	}

	@Override
	public int getProofOfWorkDifficulty() {
		return block.getProofOfWorkDifficulty();
	}

	@Override
	public String getPreviousHash() {
		return block.getPreviousHash();
	}

	@Override
	public String getHash() {
		return block.getHash();
	}

	@Override
	public long getMiningDuration() {
		return block.getMiningDuration();
	}

	@Override
	public User getMiner() {
		return block.getMiner();
	}

	@Override
	public T getData() {
		return block.getData();
	}

	@Override
	public String toString() {
		return block.toString();
	}
}
