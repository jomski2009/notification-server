package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/04/04.
 */
public class ResultWrapper {
    private SocialGroup socialGroup;
    private int memberCount;
    private int activityCount;

    public SocialGroup getSocialGroup() {
        return socialGroup;
    }

    public void setSocialGroup(SocialGroup socialGroup) {
        this.socialGroup = socialGroup;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }
}
