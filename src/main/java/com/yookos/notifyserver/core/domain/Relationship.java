package com.yookos.notifyserver.core.domain;

import com.mongodb.DBObject;
import org.bson.BSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by jome on 2014/04/22.
 */
public class Relationship implements DBObject {
    @Override
    public void markAsPartialObject() {

    }

    @Override
    public boolean isPartialObject() {
        return false;
    }

    @Override
    public Object put(String key, Object v) {
        return null;
    }

    @Override
    public void putAll(BSONObject o) {

    }

    @Override
    public void putAll(Map m) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public Map toMap() {
        return null;
    }

    @Override
    public Object removeField(String key) {
        return null;
    }

    @Override
    public boolean containsKey(String s) {
        return false;
    }

    @Override
    public boolean containsField(String s) {
        return false;
    }

    @Override
    public Set<String> keySet() {
        return null;
    }
}
