package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.core.domain.UserRelationship;
import com.yookos.notifyserver.services.NotificationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jome on 2014/02/25.
 */
@RestController
@RequestMapping(value = "relationships")
public class RelationshipController {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NotificationRestService notificationRestService;

    @RequestMapping("pcl/add")
    public ResponseEntity<String> addRelationship() {

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity<UserRelationship> addUserRelationship(@RequestBody UserRelationship userRelationship) {
        log.info("Incoming data: {}", userRelationship.toString());
        userRelationship.setHasdevice(false);
        UserRelationship relationship = notificationRestService.addToUserRelationship(userRelationship);
        if (relationship != null) {
            return new ResponseEntity<>(relationship, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
