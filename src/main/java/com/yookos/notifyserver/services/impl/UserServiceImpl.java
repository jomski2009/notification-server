package com.yookos.notifyserver.services.impl;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.yookos.notifyserver.core.domain.Activity;
import com.yookos.notifyserver.core.domain.User;
import com.yookos.notifyserver.rest.domain.UserResource;
import com.yookos.notifyserver.services.UserService;
import com.yookos.notifyserver.utils.ActivityConstants;
import org.apache.commons.lang.StringUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.neo4j.cypher.CypherExecutionException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestResultException;
import org.neo4j.rest.graphdb.batch.BatchRestAPI;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Service
public class UserServiceImpl implements UserService {
    Logger log = LoggerFactory.getLogger(this.getClass());
    //Logger log = Logger.getLogger(this.getClass());

    @Autowired
    BatchRestAPI batchRestAPI;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    RestAPIFacade gd;

    @Autowired
    RestCypherQueryEngine engine;

    @Autowired
    Mongo mongo;

    @Autowired
    Datastore ds;

    public User createUser(UserResource userResource) {
        User user = new User();
        BeanUtils.copyProperties(userResource, user);
        return null;

    }

    @Override
    public User createUser(User user) {

        try (Transaction tx = gd.beginTx()) {
            Map<String, Object> props = new HashMap<>();
            Map<String, Object> params = new HashMap<>();
            props.put("username", user.getUsername());
            props.put("userid", user.getUserid());
            props.put("firstname", user.getFirstName());
            props.put("lastname", user.getLastName());
            props.put("email", user.getEmail());
            props.put("creationdate", user.getCreationdate());
            props.put("name", user.getFirstName() + " " + user.getLastName());
            props.put("userenabled", true);
            props.put("age", user.getAge());
            props.put("birthdate", user.getBirthdate());
            if (user.getGender().equals(null) || user.getGender().equals("null")) {
                props.put("gender", "unknown");
            } else {
                props.put("gender", user.getGender());
            }
            props.put("lastloggedin", user.getLastLoggedIn());
            props.put("lastprofileupdate", user.getLastProfileUpdate());


            params.put("props", props);
            engine.query("create (p:Person{props})", params);
            params.clear();
            tx.success();

            //Save in mongo
            ds.save(user);
            return user;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void updateUser(User user) {

        try (Transaction tx = gd.beginTx()) {
            Map<String, Object> props = new HashMap<>();
            Map<String, Object> params = new HashMap<>();

            props.put("userid", user.getUserid());
            props.put("firstname", user.getFirstName());
            props.put("lastname", user.getLastName());
            props.put("email", user.getEmail());
            props.put("name", user.getFirstName() + " " + user.getLastName());
            props.put("userenabled", true);
            props.put("age", user.getAge());
            props.put("birthdate", user.getBirthdate());
            props.put("gender", user.getGender());
            props.put("lastprofileupdate", user.getLastProfileUpdate());
            props.put("lastloggedin", user.getLastLoggedIn());


            params.put("props", props);
            String query = "match (p:Person{userid:{userid}}) " +
                    "set p.firstname = {firstname} " +
                    "set p.lastname = {lastname} " +
                    "set p.name = {name} " +
                    "set p.age = {age} " +
                    "set p.birthdate = {birthdate} " +
                    "set p.gender = {gender} " +
                    "set p.lastloggedin = {lastloggedin} " +
                    "set p.lastprofileupdate = {lastprofileupdate} " +
                    "return p";

            engine.query(query, props);
            props.clear();
            tx.success();

            //Update in mongo
            //ds.save(user);
            //return user;
        } catch (Exception e) {
            log.error("Update User message: " + e.getMessage());
        }
    }

    public void bulkAddUsers(int start, int end) {
    }

    @Override
    public void bulkAddUsers(List<String> users) {
        int rowCount = 0;
        List<Map<String, Object>> holder = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        for (String r : users) {
            String[] rd = r.split(";");
            if (rd.length == 9) {
                User user = new User();

                user.setUserid(Long.parseLong(rd[0]));
                user.setUsername(sanitize(rd[1]));
                user.setFirstName(sanitize(rd[2]));
                user.setLastName(sanitize(rd[3]));
                user.setEmail(sanitize(rd[4]));
                boolean active = false;
                int enabled = Integer.parseInt(rd[5]);
                if (enabled == 1)
                    active = true;
                user.setEnabled(active);
                user.setCreationdate(Long.parseLong(rd[6]));
                user.setLastLoggedIn(Long.parseLong(rd[7]));
                user.setLastProfileUpdate(Long.parseLong(rd[8]));

                try {
                    Key<User> userKey = ds.save(user);
                    log.info("User with id: " + userKey.getId() + " saved.");
                } catch (MongoException me) {
                    log.error(me.getMessage());
                }
            }
        }
    }


    public void deleteUser(Long userid) {
    }

    public void follow() {

    }

    @Override
    public void addToUserGraph(List<String> rows, int start, int end) {
        int rowCount = 0;

        Map<String, Object> params = new HashMap<>();

        for (String r : rows) {
            String[] rd = r.split(";");
            System.out.println("Row count: " + (rowCount + 1) + "| " + r);
            System.out.println("Row count: " + (rowCount + 1)
                    + "| Array Length: " + rd.length);
            //holder.add(props);
            rowCount++;


            if (rd.length == 9) {
                // User user = new User();
                Map<String, Object> props = new HashMap<>();

                props.put("userid", Long.parseLong(rd[0]));
                props.put("username", sanitize(rd[1]));
                props.put("name", "");
                props.put("firstname", sanitize(rd[2]));
                props.put("lastname", sanitize(rd[3]));
                props.put("email", sanitize(rd[4]));
                boolean active = false;
                int enabled = Integer.parseInt(rd[5]);
                if (enabled == 1)
                    active = true;

                props.put("userenabled", active);
                props.put("creationdate", Long.parseLong(rd[6]));
                props.put("lastloggedin", Long.parseLong(rd[7]));
                props.put("lastprofileupdate", Long.parseLong(rd[8]));


                if (rowCount >= start && rowCount <= end) {
                    try (Transaction tx = gd.beginTx()) {
                        System.out.println("Processing row: " + rowCount);
                        params.put("props", props);
                        engine.query("create (p:Person{props})", params);
                        params.clear();
                        tx.success();
                    } catch (RestResultException rre) {
                        log.info(rre.getLocalizedMessage());
                        continue;
                    } catch (CypherExecutionException cee) {
                        log.info(cee.getLocalizedMessage());
                        continue;
                    } catch (Exception e) {
                        log.info(e.getLocalizedMessage());
                        continue;
                    }
                }

            }


            if (rowCount > end) {
                return;
            }

        }
    }

    @Override
    public void createNodes() {

        try (Transaction tx = gd.beginTx()) {
            Map<String, Object> n1 = new HashMap<>();
            n1.put("name", "Andres");
            n1.put("position", "Developer");
            n1.put("awesome", true);

            Map<String, Object> n2 = new HashMap<>();
            n2.put("name", "Michael");
            n2.put("position", "Developer");
            n2.put("children", 3);

            Map<String, Object> props = new HashMap<>();
            props.put("username", "jomski2012");
            props.put("userid", 1001L);
            props.put("email", "jome@example.com");

            Map<String, Object> params = new HashMap<>();
            List<Map<String, Object>> maps = asList(n1, n2, props);
            params.put("props", maps);

            QueryResult<Map<String, Object>> result = engine.query(
                    "create (p:Person{props})", params);
            System.out.println(result.toString());

        }

    }

    @Override
    public void addToUserGraph(List<String> rows) {
        int rowCount = 0;
        List<Integer> missedRows = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();

        for (String r : rows) {
            String[] rd = r.split(";");
            log.info("Row count: " + (rowCount + 1) + "|" + r);
            log.info("Row count: " + (rowCount + 1)
                    + "| Array Length: " + rd.length);

            if (rd.length == 8) {
                // User user = new User();
                Map<String, Object> props = new HashMap<>();

                props.put("userid", Long.parseLong(rd[0]));
                props.put("username", sanitize(rd[1]));
                props.put("firstname", sanitize(rd[2]));
                props.put("lastname", sanitize(rd[3]));
                props.put("name", sanitize(rd[2]) + " " + sanitize(rd[3]));
                props.put("email", sanitize(rd[4]));
                props.put("creationdate", Long.parseLong(rd[5]));
                props.put("lastloggedin", Long.parseLong(rd[6]));
                props.put("lastprofileupdate", Long.parseLong(rd[7]));

                props.put("userenabled", true);

                //holder.add(props);


                try (Transaction tx = gd.beginTx()) {
                    System.out.println("Processing row: " + rowCount);
                    params.put("props", props);
                    engine.query("create (p:Person{props})", params);
                    params.clear();
                    tx.success();

                } catch (RestResultException rre) {
                    log.info(rre.getLocalizedMessage());
                } catch (CypherExecutionException cee) {
                    log.info(cee.getLocalizedMessage());
                } catch (Exception e) {
                    log.info(e.getLocalizedMessage());
                }

            } else {
                //Log the violating row...
                missedRows.add(rowCount);
                log.info("Skipping row number: " + rowCount);

            }
            rowCount++;
        }

        log.info("List of missed rows: " + missedRows.toString());
    }

    @Override
    public void addActivityToGraph(List<String> activities) {
        int rowCount = 0;
        List<Integer> missedRows = new ArrayList<>();

        StringBuilder cypherQuery = new StringBuilder();

        Map<String, Object> params = new HashMap<>();

        for (String r : activities) {
            StringBuilder query = new StringBuilder();

            String[] rd = r.split(";");
            log.info("Row count: " + (rowCount + 1) + "| " + r);
            log.info("Row count: " + (rowCount + 1)
                    + "| Array Length: " + rd.length);

            Map<String, Object> props = new HashMap<>();

            props.put("activityid", Long.parseLong(rd[0]));
            props.put("objecttype", Integer.parseInt(rd[1]));
            props.put("objectid", Integer.parseInt(rd[2]));
            props.put("containertype", Integer.parseInt(rd[3]));
            props.put("containerid", Integer.parseInt(rd[4]));
            props.put("activitytype", getActivityType(Integer.parseInt(rd[5])));
            props.put("userid", Integer.parseInt(rd[6]));
            props.put("creationdate", Long.parseLong(rd[7]));

            params.put("props", props);
            params.put("userid", Integer.parseInt(rd[6]));

            query.append("match (p:Person{userid:").append(Integer.parseInt(rd[6])).append("})");
            query.append(" create unique (p)-[:created]->(a:Activity{");
            query.append("activityid:").append(Long.parseLong(rd[0])).append(",");
            query.append("objecttype:").append(Integer.parseInt(rd[1])).append(",");
            query.append("objectid:").append(Integer.parseInt(rd[2])).append(",");
            query.append("containertype:").append(Integer.parseInt(rd[3])).append(",");
            query.append("containerid:").append(Integer.parseInt(rd[4])).append(",");
            query.append("activitytype:\"").append(getActivityType(Integer.parseInt(rd[5]))).append("\",");
            query.append("creationdate:").append(Long.parseLong(rd[7])).append("})");


            //holder.add(props);
            rowCount++;

            try (Transaction tx = gd.beginTx()) {
                log.info("Processing row: " + rowCount);

                log.info(query.toString());

                engine.query(query.toString(), null);

                //engine.query("match (p:Person{userid:{userid}}) create unique (p)-[:created]->(a:Activity{props})", params);

                params.clear();
                tx.success();

            } catch (RestResultException rre) {
                log.info(rre.getMessage());
            } catch (CypherExecutionException cee) {
                log.info(cee.getMessage());
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }

        log.info("List of missed rows: " + missedRows.toString());
    }

    private String getActivityType(int i) {
        return ActivityConstants.Type.valueOf(i).name();

    }

    @Override
    public void addActivityObjectToGraph(Activity activity) {
        Map<String, Object> props = new HashMap<>();
        Map<String, Object> params = new HashMap<>();


        props.put("activityid", activity.getActivityID());
        props.put("objecttype", activity.getTargetObjectType());
        props.put("objectid", activity.getTargetObjectID());
        props.put("containertype", activity.getContainerObjectType());
        props.put("containerid", activity.getContainerObjectID());
        props.put("activitytype", activity.getType());
        props.put("userid", activity.getUserID());
        props.put("creationdate", activity.getCreationDate());


        try (Transaction tx = gd.beginTx()) {

            params.put("props", props);
            params.put("userid", activity.getUserID());

            engine.query("match (p:Person{userid:{userid}}) create unique (p)-[:created]->(a:Activity{props})", params);
            params.clear();
            tx.success();

        } catch (RestResultException rre) {
            log.info(rre.getLocalizedMessage());
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
        }
    }

    @Override
    public void addActivityObjectToGraph(List<Activity> activities) {

        taskExecutor.execute(new ProcessActivity(activities));
    }

    private String sanitize(String string) {
        return string.replaceAll("\"", StringUtils.EMPTY);
    }


    //Runnable to process the activities as they come in..
    class ProcessActivity implements Runnable {
        private List<Activity> activities;

        public ProcessActivity() {
        }

        public ProcessActivity(List<Activity> activities) {
            this.activities = activities;
        }

        @Override
        public void run() {
            Map<String, Object> params = new HashMap<>();
            //log.info("Processing Activity Collection of size: " + activities.size());

            for (Activity activity : activities) {
                activity.setProcessed(false);

                //Thinking we should be dumping the activity data in mongo?
                //And then run a scheduled task later to populate neo4j?
                Key<Activity> key = ds.save(activity);

                //log.info("Activity with id: {} saved in mongo store", (long) key.getId());

//                Map<String, Object> props = new HashMap<>();
//                //log.info("Activity: " + activity.toString());
//
//                props.put("activityid", activity.getActivityID());
//                props.put("objecttype", activity.getTargetObjectType());
//                props.put("objectid", activity.getTargetObjectID());
//                props.put("containertype", activity.getContainerObjectType());
//                props.put("containerid", activity.getContainerObjectID());
//                props.put("activitytype", activity.getType());
//                props.put("userid", activity.getUserID());
//                props.put("creationdate", activity.getCreationDate());
//
//
//                try (Transaction tx = gd.beginTx()) {
//
//                    params.put("props", props);
//                    params.put("userid", activity.getUserID());
//
//                    //will need to log the statement below...
//                    QueryResult<Map<String, Object>> query = engine.query("match (p:Person{userid:{userid}}) create unique (p)-[:created]->(a:Activity{props})", params);
//
//                    params.clear();
//                    tx.success();
//
//                } catch (RestResultException rre) {
//                    log.info(rre.getLocalizedMessage());
//                } catch (CypherExecutionException cee) {
//                    log.info(cee.getLocalizedMessage());
//                }catch(Exception e) {
//                    if ( e instanceof SocketTimeoutException){
//                        log.info(e.getLocalizedMessage());
//                    }else{
//                        log.info(e.getLocalizedMessage());
//                    }
//                }
            }
        }
    }

}
