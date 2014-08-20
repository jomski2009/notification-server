package com.yookos.notifyserver.services.impl;

import com.yookos.notifyserver.services.DbService;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class DbServiceImpl implements DbService {

	Logger log = Logger.getLogger(this.getClass());
	@Autowired
	RestGraphDatabase gd;

	@Autowired
	RestCypherQueryEngine engine;

	public String createPerson() {

		try (Transaction tx = gd.beginTx()) {
			Node node = gd.createNode();
			node.setProperty("username", "emmanuel");
			node.setProperty("userid", 1001L);
			node.setProperty("email", "emmanuel@example.com");

			tx.success();
		}

		try (Transaction tx = gd.beginTx()) {
			QueryResult<Map<String, Object>> result;
			StringBuilder builder = new StringBuilder();
			String query = "match (n:Person) return n limit 200";
			result = gd.getRestAPI().query(query, null, null);
			// result = engine.query(query, null);
			Iterator<Map<String, Object>> itr = result.iterator();

			while (itr.hasNext()) {
				Map<String, Object> item = itr.next();
				RestNode node = (RestNode) item.get("n");

				Iterator<String> keys = node.getPropertyKeys().iterator();
				builder.append("Properties for node: " + node.getId() + "\n");
				builder.append("=========================================="
						+ "\n");
				while (keys.hasNext()) {
					String key = keys.next();
					if (node.getProperty(key) != null) {
						builder.append("Key: " + key + " - Value: "
								+ node.getProperty(key).toString() + "\n");
					}
				}
				builder.append("\n");
			}

			tx.success();

			return builder.toString();
		}
	}

}
