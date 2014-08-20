package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/06/06.
 */
public class UserProfileField {
    private String value;
    private String jive_label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getJive_label() {
        return jive_label;
    }

    public void setJive_label(String jive_label) {
        this.jive_label = jive_label;
    }

    @Override
    public String toString() {
        return "{" +
                "value='" + value + '\'' +
                ", jive_label='" + jive_label + '\'' +
                '}';
    }
}
