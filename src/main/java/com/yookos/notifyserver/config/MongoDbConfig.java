package com.yookos.notifyserver.config;

import com.mongodb.MongoClient;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.UnknownHostException;

/**
 * Created by jome on 2014/04/08.
 */

@Configuration
public class MongoDbConfig {
    @Autowired
    Environment env;

    @Bean(name = "mongo")
    MongoClient mongoClient() {
        MongoClient client = null;
        try {
            client = new MongoClient(env.getProperty("mongo.db.host", "localhost"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

    @Bean
    Datastore morphia() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.yookos.notifyserver.core.domain");

        Datastore ds = morphia.createDatastore(mongoClient(), "yookosreco");
        AdvancedDatastore ads = (AdvancedDatastore) ds;
        ads.ensureIndexes();
//        ds.ensureIndex(User.class, "username", "username", true, true);
//        ads.ensureIndex(User.class, "email", "email", true, true);
//        ads.ensureIndex(User.class, "cellnumber", "cellnumber", true, true);
//        ads.ensureIndex(Group.class, "name_userid", "name, user_id", true, true);


        return ads;
    }
}