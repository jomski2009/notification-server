package com.yookos.notifyserver.controller;

import com.yookos.notifyserver.core.domain.CoreUserResource;
import com.yookos.notifyserver.core.domain.User;
import com.yookos.notifyserver.services.GroupService;
import com.yookos.notifyserver.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	UserService userService;

    @Autowired
    GroupService groupService;

	@RequestMapping("/users/add")
	public ResponseEntity<String> addUser(@RequestBody User user) {
        log.info("User Object: {}", user);
		userService.createUser(user);
		return new ResponseEntity<String>("Created...", HttpStatus.CREATED);

	}


    @RequestMapping("/users/create")
    public ResponseEntity<String> createUser(@RequestBody CoreUserResource cur) {
        log.info("Core User Object: {}", cur);
        User user = new User();

        user.setAge(cur.getAge());
        user.setBirthdate(cur.getBirthdate());
        user.setCreationdate(cur.getCreationDate());
        user.setEmail(cur.getEmail());
        user.setEnabled(cur.isEnabled());
        user.setFirstName(cur.getFirstName());
        user.setLastName(cur.getLastName());
        user.setGender(cur.getGender());
        user.setUserid(cur.getUserid());
        user.setLastLoggedIn(cur.getLastLoggedIn());
        user.setLastProfileUpdate(cur.getLastProfileUpdate());
        user.setUsername(cur.getUsername());

        userService.createUser(user);
        return new ResponseEntity<String>("Created...", HttpStatus.CREATED);

    }

	@RequestMapping("/users/createnodes")
	public ResponseEntity<String> createNodes() {
		userService.createNodes();
		return new ResponseEntity<String>("Created...", HttpStatus.CREATED);

	}

	@RequestMapping("/users/addusers/{start}/{end}")
	public ResponseEntity<String> addUsers(@PathVariable("start") int start,
			@PathVariable("end") int end) {
		userService.bulkAddUsers(start, end);
		return new ResponseEntity<String>("Created...", HttpStatus.CREATED);

	}

	@RequestMapping(value = "/users/csv/addusers", method = RequestMethod.POST)
	public ResponseEntity<Void> addUsersFromCSV(
			@RequestParam("file") MultipartFile file) {
		try {
			System.out.println("Starting csv processing...");
			List<String> rows = new ArrayList<String>();
			List<User> recipients = new ArrayList<User>();

			if (!file.isEmpty()) {
				String row = "";
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

				userService.addToUserGraph(rows);

				return new ResponseEntity<Void>(HttpStatus.CREATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);

	}

//    @RequestMapping(value = "/users/csv/addusers", method = RequestMethod.POST)
//    public ResponseEntity<Void> addUsersFromCSVWhole(
//            @RequestParam("file") MultipartFile file) {
//        try {
//            System.out.println("Starting csv processing...");
//            List<String> rows = new ArrayList<String>();
//            List<User> recipients = new ArrayList<User>();
//
//            if (!file.isEmpty()) {
//                String row = "";
//                BufferedReader br = new BufferedReader(new InputStreamReader(
//                        file.getInputStream()));
//                while ((row = br.readLine()) != null) {
//                    rows.add(row);
//                }
//                br.close();
//                System.out.println(rows.get(0));
//                rows.remove(0); // Just to get csv working... removing the
//                // header row...
//                System.out.println(rows.get(0));
//
//                //userService.addToUserGraph(rows);
//                userService.bulkAddUsers(rows);
//
//                return new ResponseEntity<Void>(HttpStatus.CREATED);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
//
//    }


    @RequestMapping("/users/follow")
	public ResponseEntity<Void> followUser() {
		userService.follow();
		return new ResponseEntity<Void>(HttpStatus.CREATED);

	}

	@RequestMapping("/users/{userid}/delete")
	public ResponseEntity<Void> deleteUser(@PathVariable("userid") Long userid) {
		userService.deleteUser(userid);
		return new ResponseEntity<Void>(HttpStatus.CREATED);

	}

    @RequestMapping("/users/user/profile/update")
    public ResponseEntity<String> updateUser(@RequestBody User user){

        String message = user.toString();
        userService.updateUser(user);

        log.info("Received User data: {}", user.toString());

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
