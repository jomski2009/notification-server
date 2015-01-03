package com.yookos.notifyserver.services.impl;

import com.yookos.notifyserver.core.domain.SocialGroupMembership;
import com.yookos.notifyserver.services.GroupService;
import org.jboss.logging.Logger;
import org.neo4j.cypher.CypherExecutionException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestResultException;
import org.neo4j.rest.graphdb.batch.BatchRestAPI;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jome on 2014/03/25.
 */

@Service
public class GroupServiceImpl implements GroupService {
    Logger log = Logger.getLogger(this.getClass());

    @Autowired
    BatchRestAPI batchRestAPI;

    @Autowired
    RestAPIFacade gd;

    @Autowired
    RestCypherQueryEngine engine;

    @Override
    public void addGroupsToGraph(List<String> rows) {
        int rowCount = 0;
        List<Map<String, Object>> holder = new ArrayList<>();
        List<Integer> missedRows = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();

        for (String r : rows) {
            String[] rd = r.split(";");
            System.out.println("Row count: " + (rowCount + 1) + "| " + r);
            System.out.println("Row count: " + (rowCount + 1)
                    + "| Array Length: " + rd.length);
            if (rd.length == 8) {
                // User user = new User();
                Map<String, Object> props = new HashMap<>();


                props.put("groupid", Long.parseLong(rd[0]));
                props.put("grouptype", Integer.parseInt(rd[1]));
                props.put("name", rd[2]);
                props.put("displayname", rd[3]);
                props.put("userid", Long.parseLong(rd[4]));
                props.put("creationdate", Long.parseLong(rd[5]));
                props.put("modificationdate", Long.parseLong(rd[6]));
                props.put("status", Integer.parseInt(rd[7]));

                //holder.add(props);
                rowCount++;

                try (Transaction tx = gd.beginTx()) {
                    System.out.println("Processing row: " + rowCount);
                    params.put("props", props);
                    engine.query("create (g:SocialGroup{props})", params);
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
                log.info("Skipping row number: " + rowCount + " and groupid: " + rd[0]);

            }

        }
    }

    class ProcessGroupMemberships implements Runnable {
        List<String> rows;

        public ProcessGroupMemberships(List<String> rows) {
            this.rows = rows;
        }

        @Override
        public void run() {
            int rowCount = 0;
            List<Map<String, Object>> holder = new ArrayList<>();
            List<Integer> missedRows = new ArrayList<>();

            Map<String, Object> params = new HashMap<>();

            for (String r : rows) {
                String[] rd = r.split(";");
                log.info("Row count: " + (rowCount + 1) + "| " + r);
                log.info("Row count: " + (rowCount + 1)
                        + "| Array Length: " + rd.length);

                // User user = new User();
                Map<String, Object> relprops = new HashMap<String, Object>();


                params.put("groupid", Long.parseLong(rd[0]));
                params.put("userid", Long.parseLong(rd[1]));

                relprops.put("membertype", Integer.parseInt(rd[2]));
                relprops.put("creationdate", Long.parseLong(rd[3]));
                relprops.put("membershipid", Integer.parseInt(rd[4]));
                params.put("relprops", relprops);

                //holder.add(props);
                rowCount++;

                try (Transaction tx = gd.beginTx()) {
                    System.out.println("Processing row: " + rowCount);

                    engine.query("match (p:Person{userid:{userid}}), (g:SocialGroup{groupid:{groupid}}) create (p)-[r:Member_of{relprops}]->(g)", params);
                    params.clear();
                    tx.success();
                } catch (RestResultException rre) {
                    log.info(rre.getLocalizedMessage());
                    params.clear();
                } catch (CypherExecutionException cee) {
                    log.info(cee.getLocalizedMessage());
                    params.clear();
                } catch (Exception e) {
                    log.info(e.getLocalizedMessage());
                    params.clear();
                }
            }
        }
    }

    class ProcessPageMemberships implements Runnable {
        List<String> rows;

        public ProcessPageMemberships(List<String> rows) {
            this.rows = rows;
        }

        @Override
        public void run() {
            int rowCount = 0;
            int totalNumber = rows.size();


            Map<String, Object> params = new HashMap<>();

            for (String r : rows) {
                String[] rd = r.split(";");
                log.info("Row " + (rowCount) + " of " + totalNumber);


                params.put("userid", Long.parseLong(rd[0]));
                params.put("browseid", Long.parseLong(rd[1]));

                //holder.add(props);
                rowCount++;

                try (Transaction tx = gd.beginTx()) {
                    log.info("Processing row: " + rowCount);

                    QueryResult<Map<String, Object>> query = engine.query("match (p:Person{userid:{userid}}), (g:Page{browseid:{browseid}}) create unique (p)-[r:Follows]->(g) return g", params);

                    params.clear();
                    tx.success();
                } catch (RestResultException rre) {
                    log.info(rre.getLocalizedMessage());
                    params.clear();
                } catch (CypherExecutionException cee) {
                    log.info(cee.getLocalizedMessage());
                    params.clear();
                } catch (Exception e) {
                    log.info(e.getLocalizedMessage());
                    params.clear();
                }
            }
        }
    }

    @Override
    public void createGroupMemberships(List<String> rows) {
        ProcessGroupMemberships groupMemberships = new ProcessGroupMemberships(rows);
        new Thread(groupMemberships).start();
    }

    @Override
    public void createSpaceFollowerships(List<String> rows) {
        ProcessPageMemberships processPageMemberships = new ProcessPageMemberships(rows);
        new Thread(processPageMemberships).start();
    }

    @Override
    public void addSpacesToGraph(List<String> rows) {
        int rowCount = 0;

        Map<String, Object> params = new HashMap<>();

        for (String r : rows) {
            String[] rd = r.split(";");
            System.out.println("Row count: " + (rowCount + 1) + "| " + r);
            System.out.println("Row count: " + (rowCount + 1)
                    + "| Array Length: " + rd.length);
            // User user = new User();
            Map<String, Object> props = new HashMap<>();


            props.put("browseid", Long.parseLong(rd[0]));
            props.put("spaceid", Integer.parseInt(rd[1]));
            props.put("name", rd[2]);
            props.put("displayname", rd[3]);
            props.put("creationdate", Long.parseLong(rd[4]));

            //holder.add(props);
            rowCount++;

            try (Transaction tx = gd.beginTx()) {
                System.out.println("Processing row: " + rowCount);
                params.put("props", props);
                engine.query("create (s:Page{props})", params);
                params.clear();
                tx.success();
            } catch (RestResultException rre) {
                log.info(rre.getLocalizedMessage());
            } catch (CypherExecutionException cee) {
                log.info(cee.getLocalizedMessage());
            } catch (Exception e) {
                log.info(e.getLocalizedMessage());
            }

        }
    }

    @Override
    public void createGroupMembership(SocialGroupMembership sgm) {
        Map<String, Object> params = new HashMap<>();

        Map<String, Object> relprops = new HashMap<String, Object>();


        params.put("groupid", sgm.getSocialGroupMember().getGroupID());
        params.put("userid", sgm.getSocialGroupMember().getUserID());

        if (sgm.getSocialGroupMember().getType().equals("CREATED")) {
            relprops.put("membertype", 1);
        } else {
            relprops.put("membertype", 0);
        }

        relprops.put("creationdate", sgm.getDateCreated());
        relprops.put("membershipid", sgm.getSocialGroupMember().getMembershipID());
        params.put("relprops", relprops);


        try (Transaction tx = gd.beginTx()) {

            engine.query("match (p:Person{userid:{userid}}), (g:SocialGroup{groupid:{groupid}}) create (p)-[r:Member_of{relprops}]->(g)", params);
            params.clear();
            tx.success();
        } catch (RestResultException rre) {
            log.info(rre.getLocalizedMessage());
            params.clear();
        } catch (CypherExecutionException cee) {
            log.info(cee.getLocalizedMessage());
            params.clear();
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            params.clear();
        }
    }

    @Override
    public void delete(SocialGroupMembership sgm) {
        //TODO: Implement a delete method here...

    }
}
