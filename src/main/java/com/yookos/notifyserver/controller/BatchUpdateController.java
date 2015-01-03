package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.services.NotificationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jome on 2014/09/05.
 * <p/>
 * This will handle all activities related to batch processing of data
 */

@RestController
public class BatchUpdateController {

    @Autowired
    NotificationRestService notificationRestService;

    @RequestMapping("notification/processblocks")
    public HttpEntity processBlockLists() {
        notificationRestService.processBlockList();
        return new ResponseEntity("In process...", HttpStatus.ACCEPTED);
    }
}
