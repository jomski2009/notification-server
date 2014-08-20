package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/06/11.
 */
public class UserExistsResult {
    private String message;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
