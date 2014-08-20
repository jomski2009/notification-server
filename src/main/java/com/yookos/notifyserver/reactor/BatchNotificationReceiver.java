package com.yookos.notifyserver.reactor;

import com.yookos.notifyserver.rest.domain.BatchNotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.event.Event;
import reactor.function.Consumer;

/**
 * Created by jome on 2014/05/17.
 */
@Service
public class BatchNotificationReceiver implements Consumer<Event<BatchNotificationResource>> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void accept(Event<BatchNotificationResource> batchNotificationResourceEvent) {
        
        log.info("Event received: {}", batchNotificationResourceEvent);
        

    }
}
