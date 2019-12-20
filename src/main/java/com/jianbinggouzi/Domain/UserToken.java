package com.jianbinggouzi.Domain;

import java.util.Date;

//保存用户Token
public class UserToken extends EntityBaseDomain {

	private User user;

	private Date createTime;

	private Integer liveMinutes;

	private String token;

	public UserToken() {

	}

	public UserToken(User user, Date createTime, Integer liveMinutes, String token) {

		this.user = user;
		this.createTime = createTime;
		this.liveMinutes = liveMinutes;
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getLiveMinutes() {
		return liveMinutes;
	}

	public void setLiveMinutes(Integer liveMinutes) {
		this.liveMinutes = liveMinutes;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
