package com.jianbinggouzi.AOP;

import org.springframework.beans.factory.annotation.Autowired;

import com.jianbinggouzi.Service.UserService;
import com.jianbinggouzi.Service.UserTokenService;

public class UserAdvice {

	private UserTokenService userTokenService;

	private UserService UserService;

	@Autowired
	public void setUserTokenService(UserTokenService userTokenService) {
		this.userTokenService = userTokenService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		UserService = userService;
	}

	public void setUserFromToken() {
		System.out.println("setUserToken ---------------");
	}

}
