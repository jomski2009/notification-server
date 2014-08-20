package com.yookos.notifyserver.core.domain;

import java.util.List;

/**
 * Created by jome on 2014/06/06.
 */
public class UserRegistrationObject {
    private List<UserEmailObject> emails;
    private JiveUserRegistrationObject jive;
    private List<UserProfileField> profile;
    private UserNameObject name;

    public List<UserEmailObject> getEmails() {
        return emails;
    }

    public void setEmails(List<UserEmailObject> emails) {
        this.emails = emails;
    }

    public JiveUserRegistrationObject getJive() {
        return jive;
    }

    public void setJive(JiveUserRegistrationObject jive) {
        this.jive = jive;
    }

    public List<UserProfileField> getProfile() {
        return profile;
    }

    public void setProfile(List<UserProfileField> profile) {
        this.profile = profile;
    }

    public UserNameObject getName() {
        return name;
    }

    public void setName(UserNameObject name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserRegistrationObject{" +
                "emails=" + emails +
                ", jive=" + jive +
                ", profile=" + profile +
                ", name=" + name +
                '}';
    }
}
