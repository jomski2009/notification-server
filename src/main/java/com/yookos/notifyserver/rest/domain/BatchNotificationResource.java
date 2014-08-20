package com.yookos.notifyserver.rest.domain;

/**
 * Created by jome on 2014/05/13.
 */
public class BatchNotificationResource {
    private BatchNotification notification;
    private String cmd;

    public BatchNotification getNotification() {
        return notification;
    }

    public void setNotification(BatchNotification notification) {
        this.notification = notification;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "BatchNotificationResource{" +
                "notification=" + notification +
                ", cmd='" + cmd + '\'' +
                '}';
    }
}
