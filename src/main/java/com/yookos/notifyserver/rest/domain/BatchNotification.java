package com.yookos.notifyserver.rest.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by jome on 2014/05/13.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchNotification {

    private List<Long> userIDs;
    private BatchNotificationContent content;
    public List<Long> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<Long> userIDs) {
        this.userIDs = userIDs;
    }

    public BatchNotificationContent getContent() {
        return content;
    }

    public void setContent(BatchNotificationContent content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BatchNotification{" +
                "userIDs:" + userIDs +
                ", content:" + content +
                '}';
    }
}
