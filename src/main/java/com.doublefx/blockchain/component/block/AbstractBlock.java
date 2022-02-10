package com.doublefx.blockchain.component.block;

import com.doublefx.blockchain.example.common.User;
import com.doublefx.blockchain.component.mining.MiningTask.ProofOfWork;

import java.io.Serial;
import java.math.BigInteger;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.Optional.ofNullable;

public abstract class AbstractBlock<T> implements Block<List<T>> {
	@Serial
	private static final long serialVersionUID = 1L;

	public static final String ROOT_INITIAL_HASH = "0";

	protected static final String BLOCK_DESCRIPTION =
		"""
		Block:
		Created by miner # %s
		Id: %s
		Timestamp: %s
		Magic number: %s
		Hash of the previous block:
		%s
		Hash of the block:
		%s
		Block data: %s
		Block was generating for %s seconds
		""";

	protected final BigInteger id;
	protected final long       timestamp;
	protected final String     previousHash;
	protected final List<T>    data;

	private ProofOfWork proofOfWork;
	private long        miningDuration;

	public AbstractBlock(BigInteger id, String previousHash, List<T> data) {
		this.id           = id;
		this.previousHash = previousHash;
		this.timestamp    = currentTimeMillis();
		this.data         = data;
	}

	@Override
	public String toString() {
		return String.format(BLOCK_DESCRIPTION,
		                     getMiner().getName(),
		                     getId(),
		                     getTimestamp(),
		                     getNonce(),
		                     getPreviousHash(),
		                     getHash(),
		                     formatData(getData()),
		                     getMiningDuration() / 1_000);
	}

	protected abstract String formatData(List<T> data);

	@Override
	public List<T> getData() {
		return data;
	}

	public void setProofOfWork(ProofOfWork proofOfWork) {
		if (ofNullable(this.proofOfWork).isEmpty()) {
			this.proofOfWork    = proofOfWork;
			this.miningDuration = currentTimeMillis() - timestamp;
		}
	}

	public boolean isProven() {
		return ofNullable(proofOfWork).isPresent();
	}

	public BigInteger getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public long getNonce() {
		return proofOfWork.nonce();
	}

	public int getProofOfWorkDifficulty() {
		return proofOfWork.difficulty();
	}

	public long getMiningDuration() {
		return miningDuration;
	}

	public String getHash() {
		return proofOfWork.hash();
	}

	public User getMiner() {
		return proofOfWork.miner();
	}
}
