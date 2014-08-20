package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/02/27.
 */
public class PCLResult {
    private String username;
    private String status;
    private boolean following;
    private long creationdate;
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public long getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(long creationdate) {
        this.creationdate = creationdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "PCLResult{" +
                "username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", following=" + following +
                ", creationdate=" + creationdate +
                ", email='" + email + '\'' +
                '}';
    }
}
