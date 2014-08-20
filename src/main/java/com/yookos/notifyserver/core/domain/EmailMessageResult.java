package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/06/10.
 */
public class EmailMessageResult {
    private String message;
    private boolean success;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
