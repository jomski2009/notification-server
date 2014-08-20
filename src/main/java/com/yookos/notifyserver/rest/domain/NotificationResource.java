package com.yookos.notifyserver.rest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(value={"notification", "cmd"})
public class NotificationResource {
	private String cmd;
	private Notification notification;


	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
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
        return "NotificationResource{" +
                "cmd='" + cmd + '\'' +
                ", notification=" + notification +
                '}';
    }
}
