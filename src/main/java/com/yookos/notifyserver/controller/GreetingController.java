package com.yookos.notifyserver.controller;

import com.mongodb.*;
import com.yookos.notifyserver.core.domain.Greeting;
import com.yookos.notifyserver.reactor.JokePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    JokePublisher jokePublisher;

    @Autowired
    MongoClient mongo;


    @RequestMapping("/greeting")
    public
    @ResponseBody
    HttpEntity mongoGreeting() {
        DBCollection relationships = mongo.getDB("yookosreco").getCollection("relationships");

        DBCursor cursor = relationships.find(new BasicDBObject("actorid", 2017).append("hasdevice", true));
        int size = cursor.size();
        for (DBObject obj : cursor) {
            int followerid = (int) obj.get("followerid");
            log.info("Follower id: {}", followerid);
        }
        System.out.println("Cursor size is: " + size);


        return new ResponseEntity("Running mongo", HttpStatus.OK);
    }


    @RequestMapping("/greeting/{number}")
    public
    @ResponseBody
    Greeting greeting(
            @PathVariable("number") int number) {
        jokePublisher.run(number);
        return new Greeting(counter.incrementAndGet(), String.format(template,
                "This is the PCL notification server"));
    }

}
