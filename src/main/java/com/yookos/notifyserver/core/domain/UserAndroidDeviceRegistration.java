package com.yookos.notifyserver.core.domain;


/**
 * Created by jome on 2014/05/06.
 */

public class UserAndroidDeviceRegistration {

    private String gcm_regid;
    private long userid;

    public String getGcm_regid() {
        return gcm_regid;
    }

    public void setGcm_regid(String gcm_regid) {
        this.gcm_regid = gcm_regid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }
}
