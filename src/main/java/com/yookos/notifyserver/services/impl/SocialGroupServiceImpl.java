package com.yookos.notifyserver.services.impl;

import com.yookos.notifyserver.core.domain.Page;
import com.yookos.notifyserver.core.domain.PageResultWrapper;
import com.yookos.notifyserver.core.domain.ResultWrapper;
import com.yookos.notifyserver.core.domain.SocialGroup;
import com.yookos.notifyserver.services.SocialGroupService;
import org.jboss.logging.Logger;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.batch.BatchRestAPI;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocialGroupServiceImpl implements SocialGroupService {
    Logger log = Logger.getLogger(this.getClass());

    @Autowired
    BatchRestAPI batchRestAPI;

    @Autowired
    RestAPIFacade gd;

    @Autowired
    RestCypherQueryEngine engine;


    @Override
    public ResultWrapper getActivityForGroup(String displayname) {
        Map<String, Object> params = new HashMap<>();
        params.put("displayname", displayname);


        String queryStatement = "match (g:SocialGroup{displayname:{displayname}})<-[:Member_of]-(p:Person)-[:created]->(a:Activity) return g, count(distinct p) as membercount, count(a) as activitycount";

        ResultWrapper wrapper = new ResultWrapper();
        QueryResult<Map<String, Object>> result;

        try (Transaction tx = gd.beginTx()) {
            //result = restAPIFacade.query(queryStatement, params, null);

            result = engine.query(queryStatement, params);

            tx.success();
        }

        for (Map<String, Object> item : result) {
            int count1 = (int) item.get("membercount");
            int count2 = (int) item.get("activitycount");

            log.info(count1);
            log.info(count2);


            SocialGroup group = new SocialGroup();
            RestNode node = (RestNode) item.get("g");
            group.setCreationdate((long) node.getProperty("creationdate"));
            group.setDisplayname((String) node.getProperty("displayname"));
            group.setGroupid((Integer) node.getProperty("groupid"));
            group.setName((String) node.getProperty("name"));
            group.setStatus((Integer) node.getProperty("status"));

            wrapper.setSocialGroup(group);
            wrapper.setActivityCount((Integer) item.get("activitycount"));
            wrapper.setMemberCount((Integer) item.get("membercount"));

        }

        return wrapper;
    }

    @Override
    public ResultWrapper getActivityForGroup(int groupid) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupid", groupid);
        log.info("Group Id: " + groupid);


        String queryStatement = "match (g:SocialGroup{groupid:{groupid}})<-[:Member_of]-(p:Person)-[:created]->(a:Activity) return g, count(distinct p) as membercount, count(a) as activitycount";
        System.out.println(queryStatement);

        ResultWrapper wrapper = new ResultWrapper();
        QueryResult<Map<String, Object>> result;

        try (Transaction tx = gd.beginTx()) {
            //result = restAPIFacade.query(queryStatement, params, null);
            log.info("Connecting with neo4j store");
            result = engine.query(queryStatement, params);

            tx.success();
        }

        for (Map<String, Object> item : result) {
            int count1 = (int) item.get("membercount");
            int count2 = (int) item.get("activitycount");

            log.info(count1);
            log.info(count2);


            SocialGroup group = new SocialGroup();
            RestNode node = (RestNode) item.get("g");
            group.setCreationdate((long) node.getProperty("creationdate"));
            group.setDisplayname((String) node.getProperty("displayname"));
            group.setGroupid((Integer) node.getProperty("groupid"));
            group.setName((String) node.getProperty("name"));
            group.setStatus((Integer) node.getProperty("status"));

            wrapper.setSocialGroup(group);
            wrapper.setActivityCount((Integer) item.get("activitycount"));
            wrapper.setMemberCount((Integer) item.get("membercount"));

        }

        return wrapper;
    }

    @Override
    public PageResultWrapper getActivityForPage(String displayname) {
        Map<String, Object> params = new HashMap<>();
        params.put("displayname", displayname);


        String queryStatement = "MATCH (n:`Page`{displayname:{displayname}})-[:Follows]-(m:Person)-[:created]-(a:Activity) return n as page, count(distinct m) as followercount, count(a) as activitycount";
        System.out.println(queryStatement);
        PageResultWrapper wrapper = new PageResultWrapper();
        QueryResult<Map<String, Object>> result;

        try (Transaction tx = gd.beginTx()) {
            //result = restAPIFacade.query(queryStatement, params, null);
            log.info("Connecting with neo4j store");
            result = engine.query(queryStatement, params);

            tx.success();
        }

        for (Map<String, Object> item : result) {
            int count1 = (int) item.get("followercount");
            int count2 = (int) item.get("activitycount");

            log.info(count1);
            log.info(count2);


            Page page = new Page();
            RestNode node = (RestNode) item.get("page");

            page.setCreationdate((Long) node.getProperty("creationdate"));

            page.setDisplayname((String) node.getProperty("displayname"));
            page.setSpaceid((Integer) node.getProperty("spaceid"));
            page.setName((String) node.getProperty("name"));


            wrapper.setPage(page);
            wrapper.setActivityCount((Integer) item.get("activitycount"));
            wrapper.setMemberCount((Integer) item.get("followercount"));

        }

        return wrapper;
    }

    @Override
    public PageResultWrapper getActivityForPage(int spaceid) {
        Map<String, Object> params = new HashMap<>();
        params.put("spaceid", spaceid);


        String queryStatement = "MATCH (n:`Page`{spaceid:{spaceid}})-[:Follows]-(m:Person)-[:created]-(a:Activity) return n as page, count(distinct m) as followercount, count(a) as activitycount";

        PageResultWrapper wrapper = new PageResultWrapper();
        QueryResult<Map<String, Object>> result;

        try (Transaction tx = gd.beginTx()) {
            //result = restAPIFacade.query(queryStatement, params, null);
            log.info("Connecting with neo4j store");
            result = engine.query(queryStatement, params);

            tx.success();
        }

        for (Map<String, Object> item : result) {
            int count1 = (int) item.get("followercount");
            int count2 = (int) item.get("activitycount");

            log.info(count1);
            log.info(count2);


            Page page = new Page();
            RestNode node = (RestNode) item.get("page");


            page.setCreationdate((Long) node.getProperty("creationdate"));

            page.setDisplayname((String) node.getProperty("displayname"));
            page.setSpaceid((Integer) node.getProperty("spaceid"));
            page.setName((String) node.getProperty("name"));


            wrapper.setPage(page);
            wrapper.setActivityCount((Integer) item.get("activitycount"));
            wrapper.setMemberCount((Integer) item.get("followercount"));

        }

        return wrapper;
    }
}
