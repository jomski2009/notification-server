package com.yookos.notifyserver.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jome on 2014/06/06.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserNameObject {
    private String familyName;
    private String givenName;


    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFullName(){
        return this.givenName + " " + this.familyName;
    }

    @Override
    public String toString() {
        return "name{" +
                "familyName='" + familyName + '\'' +
                ", givenName='" + givenName + '\'' +
                '}';
    }
}
