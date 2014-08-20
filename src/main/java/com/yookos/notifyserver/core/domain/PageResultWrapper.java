package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/04/09.
 */
public class PageResultWrapper {
    private Page page;
    private int memberCount;
    private int activityCount;

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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
