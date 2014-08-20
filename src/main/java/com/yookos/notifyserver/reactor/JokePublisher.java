package com.yookos.notifyserver.reactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Reactor;
import reactor.event.Event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static reactor.event.selector.Selectors.$;

@Service
public class JokePublisher {
	@Autowired
	Reactor reactor;
	
	@Autowired
	JokeReceiver jokeReceiver;
	
	public void publishJokes(int numero, CountDownLatch latch) throws InterruptedException{
		long start = System.currentTimeMillis();
		
		AtomicInteger counter = new AtomicInteger(1);
		
		for (int i = 0; i<numero; i++){
			LatchCounter latchCounter = new LatchCounter(latch, counter.getAndIncrement());
			reactor.notify("jokes", Event.wrap(latchCounter));
		}
		
		latch.await();
		
		long elapsed = System.currentTimeMillis()-start;
		
		System.out.println("Elapsed time: " + elapsed + " ms");
		System.out.println("Average time per joke: " + elapsed/numero + "ms");
	}
	
	public void run(int numero){
		CountDownLatch downLatch = new CountDownLatch(numero);
		reactor.on($("jokes"), jokeReceiver);
		try {
			publishJokes(numero, downLatch);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
