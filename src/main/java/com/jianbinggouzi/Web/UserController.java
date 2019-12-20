package com.jianbinggouzi.Web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jianbinggouzi.Config.Output;
import com.jianbinggouzi.Config.UserLockStatus;
import com.jianbinggouzi.Config.UserType;
import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Domain.UserToken;
import com.jianbinggouzi.Service.UserService;
import com.jianbinggouzi.Utils.SecretKeyUtil;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
		Output.log("---------------", "注入userService成功");
	}

	@RequestMapping("/index")
	@ResponseBody
	public User index(String token, HttpServletRequest request, HttpServletResponse response, String str) {

		return getUser(token);
	}

	@RequestMapping(path = "/login")
	@ResponseBody
	public UserToken login(String phone, String password, HttpServletRequest request) throws Exception {

		User user = new User();
		user.setUserPhone(phone);
		user.setPassWord(password);

		user = userService.loginByPhone(user);
		if (user != null) {
			setUser(request, user);
			return userTokenService.getUserToken(user);

		} else
			return null;
	}

	@RequestMapping(path = "/mylogin")
	@ResponseBody
	public User loginInSercet(byte[] phone, byte[] password, HttpServletRequest request) throws Exception {

		User user = new User();
		user.setUserPhone((String) SecretKeyUtil.decodeFromBytes((phone)));
		user.setPassWord((String) SecretKeyUtil.decodeFromBytes((password)));

		user = userService.loginByPhone(user);

		if (user != null) {
			setUser(request, user);

			return user;

		} else
			return null;
	}

	/**
	 * 获取公钥
	 * 
	 * @return
	 */
	@RequestMapping(path = "/key")
	@ResponseBody
	public Map<String, String> key() {

		try {
			Map<String, String> result = new HashMap<String, String>();
			result.put("key", SecretKeyUtil.getBytesFromObject(SecretKeyUtil.getPublicKey()));
			return result;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@RequestMapping("/register")
	public String register(String userName, String password, String phone) throws Exception {
		User user = new User();
		user.setUserName(userName);
		user.setPassWord(password);
		user.setUserPhone(phone);
		user.setUserCredit(100);
		user.setLockStatus(UserLockStatus.UNLOCKED);
		user.setUserType(UserType.USER);
		User u = userService.register(user);
		if (u == null)
			return "failed";
		else
			return "your id is " + u.getEntityId();
	}
}
