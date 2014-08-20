package com.yookos.notifyserver.services.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.yookos.notifyserver.core.domain.PCLResult;
import com.yookos.notifyserver.core.domain.Username;
import com.yookos.notifyserver.services.MCCService;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MCCServiceImpl implements MCCService {
    Logger log = LoggerFactory.getLogger("MCC Services");
    @Autowired
    Datastore ds;

    @Autowired
    MongoClient client;

    @Override
    public List<PCLResult> checkCompliance(List<Username> usernames) {
        DBCollection relationships = client.getDB("yookosreco").getCollection("relationships");
        log.info("Batch size for compliance check: {}", usernames.size());

        List<PCLResult> results = new ArrayList<>();

        for (Username name : usernames) {
            DBObject obj = relationships.findOne(new BasicDBObject("username", name.getUsername()).append("actorid", 2017));
            PCLResult result = new PCLResult();

            if (obj != null) {
                result.setUsername((String) obj.get("username"));
                result.setFollowing(true);
                result.setCreationdate((Long) obj.get("creationdate"));
                result.setEmail((String) obj.get("email"));
                result.setStatus("registered and following");
            } else {
                result.setUsername(name.getUsername());
                result.setFollowing(false);
                result.setStatus("not following pastorchrislive");
                result.setCreationdate(0L);

            }

            results.add(result);
        }
        return results;
    }
}
