package com.yookos.notifyserver.reactor;

import java.util.concurrent.CountDownLatch;

public class LatchCounter {
	private CountDownLatch latch;
	private int jokeNumber;

	public LatchCounter(){}
	
	public LatchCounter(CountDownLatch latch2, int andIncrement) {
		latch = latch2;
		jokeNumber = andIncrement;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public int getJokeNumber() {
		return jokeNumber;
	}

	public void setJokeNumber(int jokeNumber) {
		this.jokeNumber = jokeNumber;
	}

}
