package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/06/06.
 */
public class EmailVerificationEntity {
    private String email;
    private String username;
    private int token;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }


    @Override
    public String toString() {
        return "EmailVerification{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", token=" + token +
                '}';
    }
}
