package com.yookos.notifyserver.reactor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;

@Configuration
public class ReactorConfig {
	
	@Bean
	Reactor createReactor() {
		Environment env = new Environment();
		return Reactors.reactor().env(env)
				.dispatcher(Environment.THREAD_POOL).get();

	}
}
