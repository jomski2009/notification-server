package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.core.domain.PCLResult;
import com.yookos.notifyserver.core.domain.UserAndroidDeviceRegistration;
import com.yookos.notifyserver.core.domain.UserRelationship;
import com.yookos.notifyserver.core.domain.Username;
import com.yookos.notifyserver.rest.domain.BatchNotificationResource;
import com.yookos.notifyserver.rest.domain.NotificationResource;
import com.yookos.notifyserver.services.MCCService;
import com.yookos.notifyserver.services.NotificationRestService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NotificationController {
    Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    NotificationRestService notificationRestService;

    @Autowired
    MCCService mccService;


    @RequestMapping(value = "/notification", method = RequestMethod.POST)
    public HttpEntity sendNotification(
            @RequestBody NotificationResource notificationResource) {

        log.info("Received notification to send: {}", notificationResource);
        notificationRestService.sendNotification(notificationResource);
        return new ResponseEntity<>(notificationResource,
                HttpStatus.OK);

    }

    @RequestMapping(value = "/notification/push", method = RequestMethod.POST)
    public HttpEntity sendTempNotifications(
            @RequestBody NotificationResource notificationResource) {

        log.info("Received notification to send: {}", notificationResource);
        notificationRestService.sendTempNotification(notificationResource);
        return new ResponseEntity<>(
                HttpStatus.ACCEPTED);

    }

    @RequestMapping(value = "/notification/all", method = RequestMethod.POST)
    public HttpEntity sendAllNotifications(
            @RequestBody BatchNotificationResource batchNotificationResource) {

        log.info("Received notification to send: {}", batchNotificationResource);
        notificationRestService.sendNotification(batchNotificationResource);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/notification/test", method = RequestMethod.POST)
    public ResponseEntity<NotificationResource> sendTestNotification(
            @RequestBody NotificationResource notificationResource) {
        //notificationResource.getNotification().getContent().setAuthorId(2017);
        log.info("Received notification to send: {}", notificationResource);
        notificationRestService.sendTestNotification(notificationResource);
        return new ResponseEntity<>(notificationResource,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/notification/addusers/{start}/{end}", method = RequestMethod.POST)
    public ResponseEntity<Void> addUsersFromCSV(@RequestParam("file") MultipartFile file, @PathVariable("start") int start, @PathVariable("end") int end) {
        try {
            System.out.println("Starting csv processing...");
            List<String> rows = new ArrayList<>();
            List<UserRelationship> recipients = new ArrayList<>();

            if (!file.isEmpty()) {
                String row;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        file.getInputStream()));
                while ((row = br.readLine()) != null) {
                    rows.add(row);
                }
                br.close();
                rows.remove(0); //Just to get csv working... removing the header row...
                int totalrows = rows.size();
                int rowcount = 1;
                for (String r : rows) {
                    if (rowcount > start) {
                        log.info("Processing row {} of {}", rowcount, totalrows);
                        String[] recipientData = r.split(";");
                        UserRelationship userrel = new UserRelationship();
                        userrel.setActorid(Integer.parseInt(recipientData[1]));
                        userrel.setFollowerid(Integer.parseInt(recipientData[2]));
                        userrel.setUsername(recipientData[3]);
                        userrel.setEmail(recipientData[4]);
                        userrel.setCreationdate(Long.parseLong(recipientData[5]));
                        userrel.setHasdevice(false);
                        userrel.setRelationshipType(0);

                        System.out.println("Adding : " + userrel.getActorid());
                        recipients.add(userrel);
                    }
                    rowcount++;
                    if (rowcount > end) {
                        break;
                    }
                }

                notificationRestService.addToUserRelationship(recipients);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/notification/updatedevices", method = RequestMethod.POST)
    public ResponseEntity<Void> updateUserDevicesFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Starting update device csv processing...");
            List<UserAndroidDeviceRegistration> rows = new ArrayList<>();

            if (!file.isEmpty()) {
                String row;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        file.getInputStream()));
                while ((row = br.readLine()) != null) {
                    //log.info("Row: {}",row);
                    String[] sp = row.split(";");
                    //log.info("Split string: {}", sp.toString());
                    if (!sanitize(sp[0]).trim().equals("null")) {
                        //System.out.println(sp[0]);
                        int user_id = Integer.parseInt(sanitize(sp[0]));
                        String regid = sp[1];
                        UserAndroidDeviceRegistration uadr = new UserAndroidDeviceRegistration();
                        uadr.setGcm_regid(regid);
                        uadr.setUserid(user_id);
                        rows.add(uadr);
                    }
                }
                br.close();


                notificationRestService.addDeviceToUserRelationship(rows);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/notification/devices/add")
    public ResponseEntity<String> addUserAndroidDevice(@RequestParam String regId, @RequestParam int userId) {
        log.info("Adding device with id: {}", regId);
        String result = notificationRestService.addorUpdateDeviceRegistration(userId, regId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/notification/devices/remove")
    public ResponseEntity<String> removeUserAndroidDevice(@RequestParam String regId, @RequestParam int userId) {
        log.info("Removing device with id: {}", regId);
        String writeResult = notificationRestService.removeDeviceRegistration(regId, userId);
        return new ResponseEntity<>(writeResult, HttpStatus.OK);
    }



    @RequestMapping(value = "relationships/pcl/add", method = RequestMethod.POST)
    public ResponseEntity<UserRelationship> addPclRelationship(@RequestBody UserRelationship userRelationship) {
        userRelationship.setHasdevice(false);
        UserRelationship relationship = notificationRestService.addToUserRelationship(userRelationship);
        if (relationship != null) {
            return new ResponseEntity<>(relationship, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "relationships/pcl/delete", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deletePclRelationship(@RequestBody UserRelationship userRelationship) {
        notificationRestService.deleteUserRelationship(userRelationship);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "mcc/checkpclfollowers", method = RequestMethod.POST)
    public ResponseEntity<List<PCLResult>> checkPclRelationship(@RequestBody List<Username> usernames) {
        List<PCLResult> results = mccService.checkCompliance(usernames);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    private String sanitize(String string) {
        String result = string.replaceAll("\"", StringUtils.EMPTY);
        return result;
    }


}
