package com.yookos.notifyserver.rest.domain;

/**
 * Created by jome on 2014/05/05.
 */
public class PushMsg {
    private long u;
    private String s;
    private long si;
    private long oi;
    private String ot;
    private String m;

    public long getU() {
        return u;
    }

    public void setU(long u) {
        this.u = u;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public long getSi() {
        return si;
    }

    public void setSi(long si) {
        this.si = si;
    }

    public long getOi() {
        return oi;
    }

    public void setOi(long oi) {
        this.oi = oi;
    }

    public String getOt() {
        return ot;
    }

    public void setOt(String ot) {
        this.ot = ot;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    @Override
    public String toString() {
        return "PushMsg{" +
                "u:" + u +
                ", s:'" + s + '\'' +
                ", si:'" + si + '\'' +
                ", oi:" + oi +
                ", ot:'" + ot + '\'' +
                ", m:'" + m + '\'' +
                '}';
    }
}
