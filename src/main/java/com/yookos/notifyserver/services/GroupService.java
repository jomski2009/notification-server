package com.yookos.notifyserver.services;

import com.yookos.notifyserver.core.domain.SocialGroupMembership;

import java.util.List;

/**
 * Created by jome on 2014/03/25.
 */
public interface GroupService {
    void addGroupsToGraph(List<String> rows);

    void createGroupMemberships(List<String> rows);
    void createSpaceFollowerships(List<String> rows);

    void addSpacesToGraph(List<String> rows);

    void createGroupMembership(SocialGroupMembership sgm);

    void delete(SocialGroupMembership sgm);
}
