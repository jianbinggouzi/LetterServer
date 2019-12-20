package com.jianbinggouzi.Web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Service.UserTokenService;

public class BaseController {
	// 用户上下文的标识字段
	private static String userContext = "USER_CONTEXT";

	protected UserTokenService userTokenService;

	@Autowired
	public void setUserTokenService(UserTokenService userTokenService) {
		this.userTokenService = userTokenService;
	}

	public static String getUserContext() {
		return userContext;
	}

	/**
	 * 在session中获取用户实例
	 * 
	 * @param request
	 * @return
	 */
	public User getUser(String token) {
		return (User) userTokenService.getUserByToken(token);
	}

	/**
	 * 设置用户实例到Session中
	 * 
	 * @param request
	 * @param user
	 */
	public void setUser(HttpServletRequest request, User user) {
		request.getSession().setAttribute(userContext, user);
	}

}
