package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/06/06.
 */
public class UserEmailObject {
    private String value;
    private String type;
    private boolean primary;
    private String jive_label;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getJive_label() {
        return jive_label;
    }

    public void setJive_label(String jive_label) {
        this.jive_label = jive_label;
    }

    @Override
    public String toString() {
        return "UserEmailObject{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", primary=" + primary +
                ", jive_label='" + jive_label + '\'' +
                '}';
    }
}
