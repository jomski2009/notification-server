package com.yookos.notifyserver.reactor;

import com.mongodb.*;
import com.yookos.notifyserver.rest.domain.*;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Reactor;
import reactor.event.Event;

import static reactor.event.selector.Selectors.$;

@Service
public class NotificationPublisher {
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Reactor reactor;

    @Autowired
    ReactorNotificationReceiver reactorNotificationReceiver;
    @Autowired
    BatchNotificationReceiver notificationBatchReceiver;


    @Autowired
    Datastore ds;

    @Autowired
    MongoClient client;


    @Autowired
    NotificationTestReceiver notificationTestReceiver;


    public void run(NotificationResource resource) {
        reactor.on($("prodnotifications"), reactorNotificationReceiver);
        log.info("Starting reactor on notification: {}", resource);
        publishNotifications(resource);
    }

    public void runBatch(BatchNotificationResource bnr) {
        reactor.on($("prodnotifications"), reactorNotificationReceiver);
        publishBatchNotifications(bnr);
    }

    public void runTest(NotificationResource resource) {
        log.info("Author id: {}", resource.getNotification().getContent().getAuthorId());
        reactor.on($("testnotifications"), notificationTestReceiver);
        publishTestNotifications(resource);
    }

    private void publishBatchNotifications(BatchNotificationResource bnr) {
        //We already have the list of ids to send here
        //All we process are those that already have reg ids (in the case of android
        //and then proceed to resend the data to chat server until we can
        //port the iOS and BB workflow here.
        DBCollection deviceCollection = client.getDB("yookosreco").getCollection("deviceregistrations");

        for (Long userid : bnr.getNotification().getUserIDs()) {
            //Does this user have a device id?
            DBObject dbObject = deviceCollection.findOne(new BasicDBObject("userid", userid));
            if (dbObject != null) {
                NData data = new NData();
                NotificationResource nr = new NotificationResource();
                Notification notification = new Notification();
                NotificationContent content = new NotificationContent();

                BeanUtils.copyProperties(bnr.getNotification().getContent(), content);
                BeanUtils.copyProperties(bnr.getNotification(), notification);
                BeanUtils.copyProperties(bnr, nr);
                notification.setContent(content);
                notification.setUserId(userid);
                nr.setNotification(notification);
                log.info("Created NotificationResource Bean: {}", nr);

                data.setNotificationResource(nr);
                data.setUserid(userid);

                reactor.notify("prodnotifications", Event.wrap(data));
            }else{
                log.info("User with id {} does not have a valid device.", userid);
            }
        }
    }

    public void publishNotifications(NotificationResource nr) {
        log.info(">>>>>>> Author id for notification: {}", nr.getNotification().getContent().getAuthorId());
        //DBCursor cursor = client.getDB("yookosreco").getCollection("relationships").find(new BasicDBObject("actorid", 2017).append("hasdevice", true)).sort(new BasicDBObject("followerid", 1));
        DBCursor cursor = client.getDB("yookosreco").getCollection("relationships").find(new BasicDBObject("actorid", nr.getNotification().getContent().getAuthorId()).append("hasdevice", true)).sort(new BasicDBObject("followerid", 1));
        log.info("Cursor size: {}", cursor.size());

        for (DBObject obj : cursor) {

            int followerid = (int) obj.get("followerid");
            if (obj.containsField("regid")) {
                String regid = (String) obj.get("regid");
                NData data = new NData(nr, followerid, regid);
                reactor.notify("prodnotifications", Event.wrap(data));
            } else {
                //log.info("Logging follower id: {} for actor {}", followerid, nr.getNotification().getContent().getAuthorId());
                NData data = new NData(nr, followerid);
                reactor.notify("prodnotifications", Event.wrap(data));
            }
        }
    }


    public void publishTestNotifications(NotificationResource nr) {

        log.info(">>>>>>> Author id for notification: {}", nr.getNotification().getContent().getAuthorId());
        //DBCursor cursor = client.getDB("yookosreco").getCollection("relationships").find(new BasicDBObject("actorid", 2017).append("hasdevice", true)).sort(new BasicDBObject("followerid", 1));
        DBCursor cursor = client.getDB("yookosreco").getCollection("relationships").find(new BasicDBObject("actorid", nr.getNotification().getContent().getAuthorId()).append("hasdevice", true)).sort(new BasicDBObject("followerid", 1));
        log.info("Cursor size: {}", cursor.size());

        for (DBObject obj : cursor) {
            int followerid = (int) obj.get("followerid");
            if (obj.containsField("regid")) {
                String regid = (String) obj.get("regid");
                NData data = new NData(nr, followerid, regid);
                reactor.notify("prodnotifications", Event.wrap(data));
            } else {
                //log.info("Logging follower id: {} for actor {}", followerid, nr.getNotification().getContent().getAuthorId());
                NData data = new NData(nr, followerid);
                reactor.notify("testnotifications", Event.wrap(data));
            }
        }

    }

}
