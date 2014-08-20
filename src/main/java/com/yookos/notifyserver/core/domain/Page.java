package com.yookos.notifyserver.core.domain;

/**
 * Created by jome on 2014/04/09.
 */
public class Page {
    private long creationdate;
    private String name;
    private String displayname;
    private int spaceid;
    private int browseid;

    public long getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(long creationdate) {
        this.creationdate = creationdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public int getSpaceid() {
        return spaceid;
    }

    public void setSpaceid(int spaceid) {
        this.spaceid = spaceid;
    }

    public int getBrowseid() {
        return browseid;
    }

    public void setBrowseid(int browseid) {
        this.browseid = browseid;
    }

    @Override
    public String toString() {
        return "Page{" +
                "creationdate=" + creationdate +
                ", name='" + name + '\'' +
                ", displayname='" + displayname + '\'' +
                ", spaceid=" + spaceid +
                ", browseid=" + browseid +
                '}';
    }
}
