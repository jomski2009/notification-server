package com.yookos.notifyserver.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by jome on 2014/06/05.
 */

@JsonIgnoreProperties(ignoreUnknown = true)

@Entity("registrations")
public class RegistrationEmailVerification {
    @Id
    private ObjectId id;
    private UserRegistrationObject reg;
    private int token;
    private long creationdate;
    private boolean tokenExpired;
    private boolean validated;
    private String lowercaseemail;

    public RegistrationEmailVerification() {
    }

    public RegistrationEmailVerification(UserRegistrationObject uro, int token, long creationdate) {
        this.reg = uro;
        this.token = token;
        this.creationdate = creationdate;
    }

    public RegistrationEmailVerification(UserRegistrationObject uro) {
        this.reg = uro;
        this.lowercaseemail = uro.getEmails().get(0).getValue().toLowerCase();
    }

    public UserRegistrationObject getReg() {
        return reg;
    }

    public void setReg(UserRegistrationObject reg) {
        this.reg = reg;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public long getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(long creationdate) {
        this.creationdate = creationdate;
    }

    public boolean isTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getLowercaseemail() {
        return lowercaseemail;
    }

    public void setLowercaseemail(String lowercaseemail) {
        this.lowercaseemail = lowercaseemail;
    }

    @Override
    public String toString() {
        return "RegistrationEmailVerification{" +
                "reg=" + reg +
                ", token=" + token +
                ", creationdate=" + creationdate +
                ", tokenExpired=" + tokenExpired +
                ", validated=" + validated +
                '}';
    }
}
