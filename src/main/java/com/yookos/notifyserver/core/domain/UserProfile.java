package com.yookos.notifyserver.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jome on 2014/06/06.
 */
public class UserProfile {
    private List<UserProfileField> profile = new ArrayList<>();

    public List<UserProfileField> getProfile() {
        return profile;
    }

    public void setProfile(List<UserProfileField> profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profile=" + profile +
                '}';
    }
}
