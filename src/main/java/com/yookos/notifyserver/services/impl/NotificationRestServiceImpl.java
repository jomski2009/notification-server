package com.yookos.notifyserver.services.impl;

import com.google.gson.Gson;
import com.mongodb.*;
import com.yookos.notifyserver.core.domain.UserAndroidDeviceRegistration;
import com.yookos.notifyserver.core.domain.UserRelationship;
import com.yookos.notifyserver.reactor.NotificationPublisher;
import com.yookos.notifyserver.rest.domain.*;
import com.yookos.notifyserver.services.NotificationRestService;
import com.yookos.notifyserver.services.NotificationSender;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mongodb.morphia.Datastore;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationRestServiceImpl implements NotificationRestService {
    private static final String URL_JSON = "http://chat.yookos.com/mobileservices/push/notifications.php";
    private final static String GCM_URL = "https://android.googleapis.com/gcm/send";
    private final static String GCM_KEY_URL = "https://android.googleapis.com/gcm/notification";
    private static final String GOOGLE_API_KEY = "key=AIzaSyClDqYSytbUpuCJYq6JMMRrfLcVJbuiPPY";
    private static final String GOOGLE_PROJECT_ID = "355368739731";
    private static final String NOTIFICATION_KEY_PREFIX= "ymAnd_";

    Logger log = LoggerFactory.getLogger(this.getClass());
    RestTemplate restTemplate = new RestTemplate();


    @Autowired
    NotificationPublisher notificationPublisher;

    @Autowired
    Datastore ds;

    @Autowired
    MongoClient client;

    @Autowired
    RestAPIFacade gd;

    @Autowired
    RestCypherQueryEngine engine;

    @Autowired
    NotificationSender notificationSender;

    @Autowired
    Gson gson;

    @Autowired
    JSONParser jsonParser;


    @Override
    public void sendNotification(NotificationResource resource) {
        log.info("Initiating notification processing of {}", resource);
        ProcessNotifications pn = new ProcessNotifications(resource);
        Thread thread = new Thread(pn);
        thread.start();
        //notificationPublisher.run(resource);
    }

    @Override
    public void sendNotification(BatchNotificationResource batchNotificationResource) {
        log.info("Initiating Batch notification processing");
        ProcessBatchNotifications pbn = new ProcessBatchNotifications(batchNotificationResource);
        new Thread(pbn).start();
    }

    @Override
    public void sendTestNotification(NotificationResource resource) {
        log.info("Initiating test notification processing");
        ProcessTestNotifications pn = new ProcessTestNotifications(resource);
        new Thread(pn).start();
    }

    @Override
    public void sendTempNotification(NotificationResource notificationResource) {
        notificationSender.sendNotification(notificationResource);
        //ProcessTempNotifications ptn = new ProcessTempNotifications(notificationResource);
        //new Thread(ptn).start();
    }


    @Override
    public void addDeviceToUserRelationship(List<UserAndroidDeviceRegistration> rows) {

        DBCollection relationships = client.getDB("yookosreco").getCollection("relationships");
        DBCollection devices;
        if (!client.getDB("yookosreco").collectionExists("androidusers")) {
            client.getDB("yookosreco").createCollection("androidusers", null);
            devices = client.getDB("yookosreco").getCollection("androidusers");
        } else {
            devices = client.getDB("yookosreco").getCollection("androidusers");
        }

        log.info("Initiating adding devices");

        for (UserAndroidDeviceRegistration reg : rows) {
            WriteResult result = relationships.update(new BasicDBObject("followerid", reg.getUserid()), new BasicDBObject("$set", new BasicDBObject("hasdevice", true)), false, true);
            log.info(result.toString());
            //1. Check if the current userid already has a notification key entry
            //2. If there is an entry, add the reg id to the reg ids associated with the notification key
            //3. If there is no associated key, generate a new notification_key_name (ya_userid, guaranteed to be unique)
            //4. Retrieve a new notification key and store the data in mongo.
            String notificationKeyName = NOTIFICATION_KEY_PREFIX + reg.getUserid();
            DBObject device = devices.findOne(new BasicDBObject("notification_key_name", notificationKeyName));

            if (device != null) {
                //Found an entry. Update regid array
                log.info(device.toString());
                devices.update(device, new BasicDBObject("$addToSet", new BasicDBObject("registration_ids", reg.getGcm_regid())));

            } else {
                //No entry found. Create new

                //Build the request object
                NotificationKeyRequest request = new NotificationKeyRequest();
                request.setNotification_key_name(notificationKeyName);
                request.getRegistration_ids().add(reg.getGcm_regid());
                request.setOperation("create");
                String reqobj = gson.toJson(request);

                //First obtain a new notification_key
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                headers.set("Authorization", GOOGLE_API_KEY);
                headers.set("project_id", GOOGLE_PROJECT_ID);

                ResponseEntity<String> responseEntity = restTemplate.exchange(GCM_KEY_URL, HttpMethod.POST, new HttpEntity<>(reqobj, headers), String.class);
                String notificationKeyResponse = responseEntity.getBody();
                //Convert to json to extract the notification key value
                try {
                    JSONObject key = (JSONObject) jsonParser.parse(notificationKeyResponse);
                    String notificationKey = key.get("notification_key").toString();
                    List<String> reg_ids = new ArrayList<>();
                    reg_ids.add(reg.getGcm_regid());
                    WriteResult writeResult = devices.insert(new BasicDBObject("notification_key_name", notificationKeyName)
                            .append("notification_key", notificationKey)
                            .append("userid", reg.getUserid())
                            .append("type", "android")
                            .append("registration_ids", reg_ids ));
                    log.info(writeResult.toString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //devices.update(new BasicDBObject("gcm_regid", reg.getGcm_regid()), new BasicDBObject("$set", new BasicDBObject("gcm_regid", reg.getGcm_regid()).append("userid", reg.getUserid())), true, false);
            //log.info(result.getLastConcern().getWString());
        }

    }

    @Override
    public String addorUpdateDeviceRegistration(int userId, String regId) {
        DBCollection devices = client.getDB("yookosreco").getCollection("androidusers");
        String notificationKeyName = NOTIFICATION_KEY_PREFIX + userId;
        String result = "";

        DBObject device = devices.findOne(new BasicDBObject("notification_key_name", notificationKeyName));

        if (device != null) {
            //Found an entry. Update regid array
            log.info(device.toString());
            devices.update(device, new BasicDBObject("$addToSet", new BasicDBObject("registration_ids", regId)));

        } else {
            //No entry found. Create new

            //Build the request object
            NotificationKeyRequest request = new NotificationKeyRequest();
            request.setNotification_key_name(notificationKeyName);
            request.getRegistration_ids().add(regId);
            request.setOperation("create");
            String reqobj = gson.toJson(request);

            //First obtain a new notification_key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.set("Authorization", GOOGLE_API_KEY);
            headers.set("project_id", GOOGLE_PROJECT_ID);

            ResponseEntity<String> responseEntity = restTemplate.exchange(GCM_KEY_URL, HttpMethod.POST, new HttpEntity<>(reqobj, headers), String.class);
            String notificationKeyResponse = responseEntity.getBody();
            //Convert to json to extract the notification key value
            try {
                JSONObject key = (JSONObject) jsonParser.parse(notificationKeyResponse);
                String notificationKey = key.get("notification_key").toString();
                List<String> reg_ids = new ArrayList<>();
                reg_ids.add(regId);
                WriteResult writeResult = devices.insert(new BasicDBObject("notification_key_name", notificationKeyName)
                        .append("notification_key", notificationKey)
                        .append("userid", userId)
                        .append("type", "android")
                        .append("registration_ids", reg_ids ));
                log.info(writeResult.toString());
                result = writeResult.toString();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public String removeDeviceRegistration(String regId, int userId) {
        //Remind Amu to send through the userid here as well.
        DBCollection devices = client.getDB("yookosreco").getCollection("androidusers");
        WriteResult result = devices.update(new BasicDBObject("userid", userId), new BasicDBObject("$pull", new BasicDBObject("registration_ids", regId)), false, true);

        //WriteResult result = devices.remove(new BasicDBObject("gcm_regid", regId));
        log.info("Reg removal result: {}", result.toString());
        return result.toString();
    }

    @Override
    public void addToUserRelationship(List<UserRelationship> userRelationships) {
        DBCollection relationships = client.getDB("yookosreco").getCollection("relationships");

        log.info("Documents in collection: {}", relationships.count());
        for (UserRelationship rel : userRelationships) {
            try {
                //Check if the inverse relationship already exists
                DBObject object = relationships.findOne(new BasicDBObject("followerid", rel.getActorid()).append("actorid", rel.getFollowerid()));
                log.info("found object: {}", object);
                if (object != null) {
                    //Modify the db object and save it, setting type as 1 (friend)
                    log.info("Updating : {}", object);

                    relationships.update(object, new BasicDBObject("$set", new BasicDBObject("type", 1)));
                    //relationships.update(object, new BasicDBObject("type", 1));
                    rel.setRelationshipType(1);
                    ds.save(rel);
                } else {
                    log.info("Saving : {}", rel);
                    ds.save(rel);
                }

            } catch (Exception e) {
                log.info("Caught Exception: {}", e.getMessage());
            }
        }

    }

    @Override
    public UserRelationship addToUserRelationship(UserRelationship relationship) {
        if (relationship.getRelationshipType() == 1) {
            int actorid = relationship.getActorid();
            int followerid = relationship.getFollowerid();
            //Save for actor...
            ds.save(relationship);
            //Reverse the ids and clear the object id
            relationship.setFollowerid(actorid);
            relationship.setActorid(followerid);
            relationship.setId(null);

            try {
                ds.save(relationship);
            } catch (Exception e) {
                log.error("Morphia error: {}", e.getMessage());
            }

            try (Transaction tx = gd.beginTx()) {
                String cypherQuery = "match (p:Person{userid:{followerid}}), (q:Person{userid:{actorid}}) create unique (p)-[:friends_with{creationdate:{creationdate}}]->(q)";
                Map<String, Object> params = new HashMap<>();
                params.put("creationdate", relationship.getCreationdate());

                params.put("followerid", relationship.getFollowerid());
                params.put("actorid", relationship.getActorid());

                QueryResult<Map<String, Object>> queryResult = engine.query(cypherQuery, params);

                tx.success();
            } catch (Exception e) {
                log.error("Neo4j exception: {}", e.getMessage());
            }
        } else {
            ds.save(relationship);

            try (Transaction tx = gd.beginTx()) {
                String cypherQuery = "match (p:Person{userid:{followerid}}), (q:Person{userid:{actorid}}) create unique (p)-[:Follows{creationdate:{creationdate}}]->(q)";
                Map<String, Object> params = new HashMap<>();
                params.put("creationdate", relationship.getCreationdate());

                params.put("followerid", relationship.getFollowerid());
                params.put("actorid", relationship.getActorid());

                engine.query(cypherQuery, params);

                tx.success();
            } catch (Exception e) {
                log.error("Neo4j exception: {}", e.getMessage());
            }
        }

        return relationship;

    }

    @Override
    public void deleteUserRelationship(UserRelationship relationship) {

        DBCollection relationships = client.getDB("yookosreco").getCollection("relationships");
        relationships.remove(new BasicDBObject("actorid", relationship.getActorid())
                .append("followerid", relationship.getFollowerid()));
    }

    private void processNotifications(NotificationResource nr) {
        log.info(">>>>>>> Author id for notification: {}", nr.getNotification().getContent().getAuthorId());
        //DBCursor cursor = client.getDB("yookosreco").getCollection("relationships")
        // .find(new BasicDBObject("actorid", 2017)
        // .append("hasdevice", true)).sort(new BasicDBObject("followerid", 1));

        DBCursor cursor = client.getDB("yookosreco").getCollection("relationships")
                .find(new BasicDBObject("actorid", nr.getNotification().getContent().getAuthorId())
                        .append("hasdevice", true)).sort(new BasicDBObject("followerid", 1));

        log.info("Cursor size: {}", cursor.size());

        for (DBObject obj : cursor) {

            int followerid = (int) obj.get("followerid");
            if (obj.containsField("regid")) {
                String regid = (String) obj.get("regid");
                NData data = new NData(nr, followerid, regid);
                doPush(data);
            } else {
                //log.info("Logging follower id: {} for actor {}", followerid, nr.getNotification().getContent().getAuthorId());
                NData data = new NData(nr, followerid);
                doPush(data);
            }
        }
    }

    private void doPush(NData data) {
        DBCollection devices = client.getDB("yookosreco").getCollection("androidusers");

        NotificationResource resource = data.getNotificationResource();

        NotificationContent content = data.getNotificationResource().getNotification().getContent();
        Notification notification = new Notification();
        notification.setUserId(data.getUserid());

        notification.setContent(content);
        resource.setNotification(notification);

        //resource.getNotification().setUserId(userid);
        Gson gsonObject = new Gson();
        PushNotification pn = new PushNotification();
        PushData pd = new PushData();
        PushMsg msg = new PushMsg();

        msg.setM(resource.getNotification().getContent().getAlertMessage());
        msg.setOi(resource.getNotification().getContent().getObjectId());
        if (resource.getNotification().getContent().getObjectType().trim().equals("post")) {
            msg.setOt("blogpost");
        } else {
            msg.setOt(resource.getNotification().getContent().getObjectType());
        }
        msg.setS(resource.getNotification().getContent().getSenderDisplayName());
        msg.setSi(resource.getNotification().getContent().getAuthorId());
        msg.setU(resource.getNotification().getUserId());


        pd.setMsg(msg);
        pn.setData(pd);

        DBCursor reg = devices.find(new BasicDBObject("userid", msg.getU()));


        for (DBObject o : reg) {
            String notification_key = (String) o.get("notification_key");
            String notification_key_name = (String) o.get("notification_key_name");
            pn.setNotification_key_name(notification_key_name);
            pn.setNotification_key(notification_key);

            ArrayList<String> registration_ids = (ArrayList<String>) o.get("registration_ids");
            if (registration_ids != null) {
//                System.out.println("Registration id: " + regid);
                pn.setRegistration_ids(registration_ids);

                //log.info("Built up resource for {} is {}", t.getData().getUserid(), resource.toString());
                //String requestObject = gsonObject.toJson(resource);
                String pushObject = gsonObject.toJson(pn);

                //log.info(userid + " is the passed id for " + requestObject);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                headers.set("Authorization", GOOGLE_API_KEY);
                //Add the project ID header here

                log.info(pushObject);
                ResponseEntity<String> responseEntity = restTemplate.exchange(GCM_URL, HttpMethod.POST, new HttpEntity<>(pushObject, headers), String.class);
                log.info(responseEntity.getBody());
            }

        }
    }

    private class ProcessNotifications implements Runnable {
        //We are seeing issues with reactor sending out messages many times for PCL recently
        //and I will need to explore other traditional methods for sending out the pushes...

        NotificationResource resource;

        public ProcessNotifications(NotificationResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            //notificationPublisher.run(resource);
            processNotifications(resource);
        }
    }

    class ProcessTempNotifications implements Runnable {
        RestTemplate template;
        NotificationResource resource;

        public ProcessTempNotifications(NotificationResource resource) {
            this.resource = resource;
            template = new RestTemplate();
        }

        @Override
        public void run() {
            Gson gson = new Gson();
            String json = gson.toJson(resource);


            //String result = template.postForObject(URL_JSON, json, String.class);
            HttpEntity postObject = new HttpEntity(json);

            ResponseEntity<String> result = template.exchange(URL_JSON, HttpMethod.POST, postObject, String.class);


            if (result != null) {
                log.info("Returned result from chat server: {}", result.getStatusCode());
            }

            //ResponseEntity<String> responseEntity = template.exchange(GCM_URL, HttpMethod.POST, new HttpEntity<>(pushObject, headers), String.class);

            //notificationPublisher.run(resource);
        }
    }

    class ProcessBatchNotifications implements Runnable {
        BatchNotificationResource bnr;

        public ProcessBatchNotifications(BatchNotificationResource batchNotificationResource) {
            this.bnr = batchNotificationResource;
        }

        @Override
        public void run() {
            notificationPublisher.runBatch(bnr);
        }
    }

    class ProcessTestNotifications implements Runnable {
        NotificationResource resource;

        public ProcessTestNotifications(NotificationResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            notificationPublisher.runTest(resource);
        }
    }

}
