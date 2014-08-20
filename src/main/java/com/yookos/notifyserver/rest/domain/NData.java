package com.yookos.notifyserver.rest.domain;

public class NData {
	private NotificationResource notificationResource;
	private long userid;
    private String regid;
	
	

	public NData() {
	}

	public NData(NotificationResource notificationResource, long userid) {
		this.notificationResource = notificationResource;
		this.userid = userid;
	}

    public NData(NotificationResource notificationResource, long userid, String regid) {
        this.notificationResource = notificationResource;
        this.userid = userid;
        this.regid = regid;
    }


    public NotificationResource getNotificationResource() {
		return notificationResource;
	}

	public void setNotificationResource(
			NotificationResource notificationResource) {
		this.notificationResource = notificationResource;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }
}
