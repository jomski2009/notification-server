package com.yookos.notifyserver.neo4j.enums;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
	Friends_with, Is_Married_To, Likes, Member_Of, Follows
}
