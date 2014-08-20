package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.core.domain.Activity;
import com.yookos.notifyserver.services.GroupService;
import com.yookos.notifyserver.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jome on 2014/04/07.
 */

@Controller
public class ActivityController {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @RequestMapping(value = "/activity/add", method = RequestMethod.POST)
    public ResponseEntity<Void> addActivitiesFromCore(@RequestBody List<Activity> activities) {
        //log.info("Processing Activities: {}", activities.toString());
        try {
            userService.addActivityObjectToGraph(activities);

            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/activity/csv/add", method = RequestMethod.POST)
    public ResponseEntity<Void> addActivities(
            @RequestParam("file") MultipartFile file) {
        try {
            log.info("Starting csv processing...");
            List<String> rows = new ArrayList<>();

            if (!file.isEmpty()) {
                String row;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        file.getInputStream()));
                while ((row = br.readLine()) != null) {
                    rows.add(row);
                }
                br.close();
                System.out.println(rows.get(0));
                rows.remove(0); // Just to get csv working... removing the
                // header row...
                System.out.println(rows.get(0));

                userService.addActivityToGraph(rows);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/activity/addsingle", method = RequestMethod.POST)
    public ResponseEntity<Void> addActivityFromCore(@RequestBody Activity activity) {
        try {
            userService.addActivityObjectToGraph(activity);

            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }


}
