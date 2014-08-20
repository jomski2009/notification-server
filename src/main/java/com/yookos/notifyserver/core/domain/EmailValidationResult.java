package com.yookos.notifyserver.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jome on 2014/06/06.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailValidationResult {
    private boolean valid;
    private String message;
    private int code;
    private UserRegistrationObject registration;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

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

    public UserRegistrationObject getRegistration() {
        return registration;
    }

    public void setRegistration(UserRegistrationObject registration) {
        this.registration = registration;
    }
}
