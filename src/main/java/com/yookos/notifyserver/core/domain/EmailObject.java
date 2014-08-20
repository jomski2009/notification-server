package com.yookos.notifyserver.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by jome on 2014/06/05.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailObject {
    private String ToEmail;
    private String ToName;
    private String FromName;
    private String FromEmail;
    private String Subject;
    private String HTML;
    private String Text;
    private String ReplyTo;

    public String getToEmail() {
        return ToEmail;
    }

    public void setToEmail(String toEmail) {
        ToEmail = toEmail;
    }

    public String getToName() {
        return ToName;
    }

    public void setToName(String toName) {
        ToName = toName;
    }

    public String getFromName() {
        return FromName;
    }

    public void setFromName(String fromName) {
        FromName = fromName;
    }

    public String getFromEmail() {
        return FromEmail;
    }

    public void setFromEmail(String fromEmail) {
        FromEmail = fromEmail;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getHTML() {
        return HTML;
    }

    public void setHTML(String HTML) {
        this.HTML = HTML;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getReplyTo() {
        return ReplyTo;
    }

    public void setReplyTo(String replyTo) {
        ReplyTo = replyTo;
    }

    @Override
    public String toString() {
        return "EmailObject{" +
                "ToEmail='" + ToEmail + '\'' +
                ", ToName='" + ToName + '\'' +
                ", FromName='" + FromName + '\'' +
                ", FromEmail='" + FromEmail + '\'' +
                ", Subject='" + Subject + '\'' +
                ", HTML='" + HTML + '\'' +
                ", Text='" + Text + '\'' +
                ", ReplyTo='" + ReplyTo + '\'' +
                '}';
    }
}
