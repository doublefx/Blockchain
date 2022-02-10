package com.doublefx.blockchain.component.block;

import java.io.Serial;

public class BlockWithDifficultyInfo<T> extends BlockDecorator<T> {
	@Serial
	private static final long serialVersionUID = 1L;

	private String nextBlockDifficultyInfo = "";

	public static <T> Block<T> of(Block<T> block) {
		return new BlockWithDifficultyInfo<>(block);
	}

	private BlockWithDifficultyInfo(Block<T> block) {
		super(block);
	}

	public void setNextBlockDifficultyInfo(String nextBlockDifficultyInfo) {
		this.nextBlockDifficultyInfo = nextBlockDifficultyInfo;
	}

	@Override
	public String toString() {
		return block.toString() + nextBlockDifficultyInfo;
	}
}
