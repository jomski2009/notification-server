package com.yookos.notifyserver.reactor;

import com.google.gson.Gson;
import com.mongodb.*;
import com.yookos.notifyserver.rest.domain.*;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.event.Event;
import reactor.function.Consumer;

@Service
public class NotificationTestReceiver implements Consumer<Event<NData>> {
    private static final String URL_JSON = "http://chat.yookos.com/mobileservices/push/pclnotifications.php";
    private final static String GCM_URL = "https://android.googleapis.com/gcm/send";
    private static final   String GOOGLE_API_KEY = "key=AIzaSyClDqYSytbUpuCJYq6JMMRrfLcVJbuiPPY";
    Logger log = LoggerFactory.getLogger(this.getClass());
    RestTemplate template = new RestTemplate();

    @Autowired
    Datastore ds;

    @Autowired
    MongoClient mongo;

    @Override
    public void accept(Event<NData> t) {

        DBCollection devices = mongo.getDB("yookosreco").getCollection("deviceregistrations");

        NotificationResource resource = t.getData().getNotificationResource();

        NotificationContent content = t.getData().getNotificationResource().getNotification().getContent();
        Notification notification = new Notification();
        notification.setUserId(t.getData().getUserid());
        notification.setContent(content);
        resource.setNotification(notification);

        long userid = t.getData().getUserid();
        //resource.getNotification().setUserId(userid);
        Gson gsonObject = new Gson();
        PushNotification pn = new PushNotification();
        PushData pd = new PushData();
        PushMsg msg = new PushMsg();

        msg.setM(resource.getNotification().getContent().getAlertMessage());
        msg.setOi(resource.getNotification().getContent().getObjectId());
        if (resource.getNotification().getContent().getObjectType().trim().equals("post")){
            msg.setOt("blogpost");
        } else{
            msg.setOt(resource.getNotification().getContent().getObjectType());
        }
        msg.setS(resource.getNotification().getContent().getSenderDisplayName());
        msg.setSi(resource.getNotification().getContent().getAuthorId());
        msg.setU(resource.getNotification().getUserId());


        pd.setMsg(msg);
        pn.setData(pd);

        DBCursor reg = devices.find(new BasicDBObject("userid", msg.getU()));


        for (DBObject o : reg) {
            String regid = (String) o.get("gcm_regid");
            if (regid != null) {
                pn.getRegistration_ids().add(regid);

                String pushObject = gsonObject.toJson(pn);

                log.info("Push data is " + pushObject);
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
                headers.set("Authorization", GOOGLE_API_KEY);


//                ResponseEntity<String> responseEntity = template.exchange(GCM_URL, HttpMethod.POST, new HttpEntity<>(pushObject, headers), String.class);
//                log.info(responseEntity.getBody());
            }

        }


    }

}
