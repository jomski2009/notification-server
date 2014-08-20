package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/06/06.
 */
public class JiveUserRegistrationObject {
    private String password;
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "jive{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
