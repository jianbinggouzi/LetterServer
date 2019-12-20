package com.jianbinggouzi.Service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jianbinggouzi.Config.UserLockStatus;
import com.jianbinggouzi.Dao.UserDao;
import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Exception.ExceptionWithMessage;

@Service
public class UserService {

	private UserDao userDao;

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * 设置用户锁定状态
	 * 
	 * @param user
	 * @param status
	 * @return 是否成功
	 */
	public boolean setUserLockStatus(User user, UserLockStatus status) {
		User u = userDao.get(user.getEntityId());
		u.setLockStatus(status);
		userDao.update(u);
		return getUserLockStatus(user) == status;
	}

	/**
	 * 获取用户锁定状态
	 * 
	 * @param user
	 * @return
	 */
	public UserLockStatus getUserLockStatus(User user) {
		return userDao.get(user).getLockStatus();
	}

	/**
	 * 用户注册
	 * 
	 * @param user
	 * @return user实例
	 */
	public User register(User user) throws ExceptionWithMessage {
		User u = new User();
		u.setUserPhone(user.getUserPhone());
		if (userDao.findByExample(u).size() > 0)
			throw new ExceptionWithMessage("手机号已存在");
		else {
			Serializable key = userDao.save(user);
			user.setEntityId((String) key);
			return user;
		}
	}

	/**
	 * 用户手机号登录
	 * 
	 * @param user
	 * @return
	 */
	public User loginByPhone(User user) throws ExceptionWithMessage {
		List users = userDao.findByExample(user);
		if (users.size() != 1)
			throw new ExceptionWithMessage("登录信息已存在");
		return (User) (users.get(0));
	}

}
