package com.yookos.notifyserver.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jome on 2014/04/25.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialGroupMembership {
    private SocialGroupMember socialGroupMember;
    private long dateCreated;

    public SocialGroupMember getSocialGroupMember() {
        return socialGroupMember;
    }

    public void setSocialGroupMember(SocialGroupMember socialGroupMember) {
        this.socialGroupMember = socialGroupMember;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}
