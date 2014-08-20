package com.yookos.notifyserver.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class JokeReceiver implements Consumer<Event<LatchCounter>> {
	Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String JOKE_URL = "http://api.icndb.com/jokes/random";

	RestTemplate restTemplate = new RestTemplate();

	@Override
	public void accept(Event<LatchCounter> t) {

		JokeResource jokeResource = restTemplate.getForObject(JOKE_URL,
				JokeResource.class);
		log.info("Joke " + t.getData().getJokeNumber() + ": "
				+ jokeResource.getValue().getJoke());
		t.getData().getLatch().countDown();
	}
}
