package com.yookos.notifyserver.rest.domain;

/**
 * Created by jome on 2014/05/05.
 */
public class PushData {
    private PushMsg msg;

    public PushMsg getMsg() {
        return msg;
    }

    public void setMsg(PushMsg msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "PushData{" +
                "msg:" + msg +
                '}';
    }
}
