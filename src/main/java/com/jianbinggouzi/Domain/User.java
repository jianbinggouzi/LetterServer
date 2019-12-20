package com.jianbinggouzi.Domain;

import com.jianbinggouzi.Config.UserLockStatus;
import com.jianbinggouzi.Config.UserType;

//用户
public class User extends EntityBaseDomain {

	private String userName;

	private String passWord;

	private String userPhone;
	// 用户类型
	private UserType userType;
	// 用户锁定状态
	private UserLockStatus lockStatus;
	// 用户积分值
	private Integer userCredit;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public UserLockStatus getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(UserLockStatus lockStatus) {
		this.lockStatus = lockStatus;
	}

	public Integer getUserCredit() {
		return userCredit;
	}

	public void setUserCredit(Integer userCredit) {
		this.userCredit = userCredit;
	}

}
