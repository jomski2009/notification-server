package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.core.domain.PageResultWrapper;
import com.yookos.notifyserver.core.domain.ResultWrapper;
import com.yookos.notifyserver.core.domain.SocialGroupMembership;
import com.yookos.notifyserver.core.domain.User;
import com.yookos.notifyserver.services.GroupService;
import com.yookos.notifyserver.services.SocialGroupService;
import com.yookos.notifyserver.services.UserService;
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

/**
 * Created by jome on 2014/04/04.
 */

@RestController
public class SocialGroupController {
    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SocialGroupService socialGroupService;

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @RequestMapping("groups/{groupid}/getActivity")
    public HttpEntity getActivityForGroup(@PathVariable("groupid") int groupid) {
        ResultWrapper activityForGroup = socialGroupService.getActivityForGroup(groupid);

        if (activityForGroup != null) {
            return new ResponseEntity(activityForGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity("Group with id " + groupid + " not found", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("groups/displayname/{displayname}/getActivity")
    public HttpEntity getActivityForGroup(@PathVariable("displayname") String displayname) {
        ResultWrapper activityForGroup = socialGroupService.getActivityForGroup(displayname);

        if (activityForGroup != null) {
            return new ResponseEntity(activityForGroup, HttpStatus.OK);
        } else {
            return new ResponseEntity("Group with id " + displayname + " not found", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("pages/displayname/{displayname}/getActivity")
    public HttpEntity getActivityForPage(@PathVariable("displayname") String displayname) {
        PageResultWrapper activityForPage = socialGroupService.getActivityForPage(displayname);

        if (activityForPage != null) {
            return new ResponseEntity(activityForPage, HttpStatus.OK);
        } else {
            return new ResponseEntity("Page with display name, " + displayname + " not found", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping("pages/{spaceid}/getActivity")
    public HttpEntity getActivityForPage(@PathVariable("spaceid") int spaceid) {
        PageResultWrapper activityForPage = socialGroupService.getActivityForPage(spaceid);

        if (activityForPage != null) {
            return new ResponseEntity(activityForPage, HttpStatus.OK);
        } else {
            return new ResponseEntity("Page with id " + spaceid + " not found", HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "/groups/csv/addgroups", method = RequestMethod.POST)
    public ResponseEntity<Void> addGroupsFromCSVWhole(
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Starting csv processing...");
            List<String> rows = new ArrayList<String>();
            List<User> recipients = new ArrayList<User>();

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

                groupService.addGroupsToGraph(rows);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/spaces/csv/addspaces", method = RequestMethod.POST)
    public ResponseEntity<Void> addSpacesFromCSV(
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Starting csv processing...");
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

                groupService.addSpacesToGraph(rows);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/spaces/csv/addfollowers", method = RequestMethod.POST)
    public ResponseEntity<Void> createSpaceFollowerships(
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Starting csv processing...");
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

                groupService.createSpaceFollowerships(rows);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/groups/csv/addmembers", method = RequestMethod.POST)
    public ResponseEntity<Void> createGroupmemberships(
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

                groupService.createGroupMemberships(rows);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/groups/members/add", method = RequestMethod.POST)
    public ResponseEntity<Void> createGroupmembership( @RequestBody SocialGroupMembership sgm) {
        try {

                groupService.createGroupMembership(sgm);

                return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/groups/members/delete", method = RequestMethod.POST)
    public ResponseEntity<Void> deleteGroupmembership( @RequestBody SocialGroupMembership sgm) {
        try {

            groupService.delete(sgm);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }



}
