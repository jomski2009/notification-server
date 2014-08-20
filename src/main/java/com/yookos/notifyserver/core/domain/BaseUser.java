package com.yookos.notifyserver.core.domain;

public abstract class BaseUser {
	protected long userid;

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}
}
