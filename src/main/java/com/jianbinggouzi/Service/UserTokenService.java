package com.jianbinggouzi.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jianbinggouzi.Dao.UserTokenDao;
import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Domain.UserToken;

@Service
public class UserTokenService extends BaseService {

	private UserTokenDao userTokenDao;

	@Autowired
	public void setUserTokenDao(UserTokenDao userTokenDao) {
		this.userTokenDao = userTokenDao;
	}

	/**
	 * 用于登录时返回用户的Token，存在返回，不存在则创建后返回
	 * 
	 * @param user
	 * @param macAddress
	 * @return userToken
	 */
	public UserToken getUserToken(User user) {
		String uuid = UUID.randomUUID().toString().replace("-", "");

		Map<String, String> map = new HashMap<String, String>();
		map.put("user", user.getEntityId());
		UserToken userToken = null;

		try {
			userToken = userTokenDao.queryByMap(map).get(0);
			resetTokenTime(userToken.getToken());
		} catch (Exception e) {
			e.printStackTrace();
			userToken = new UserToken(user, new Date(), 180 * 1440, uuid);
			userTokenDao.save(userToken);
		} finally {
			return userToken;
		}
	}

	/**
	 * 根据token获取用户
	 * 
	 * @param token
	 * @param macAddress
	 * @return
	 */
	public User getUserByToken(String token) {

		Map<String, String> map = new HashMap<>();
		map.put("token", token);
		List<UserToken> list = userTokenDao.queryByMap(map);
		return list.size() == 1 ? (User) list.get(0).getUser() : null;
	}

	/**
	 * 获取Token是否有效
	 * 
	 * @param token
	 * @param macAddress
	 * @return
	 */
	public boolean getLivedByToken(String token) {
		Map<String, String> map = new HashMap<>();
		map.put("token", token);
		List<UserToken> list = userTokenDao.queryByMap(map);

		if (list.size() == 0)
			return false;
		UserToken userToken = list.get(0);
		if (userToken.getCreateTime().getMinutes() + userToken.getLiveMinutes() < (new Date()).getMinutes())
			return false;
		return true;
	}

	/**
	 * 重新设置token的创建时间
	 * 
	 * @param token
	 * @param macAddress
	 */
	public void resetTokenTime(String token) {
		UserToken example = new UserToken(null, null, null, token);
		List<UserToken> list = userTokenDao.findByExample(example);
		if (list.size() == 0)
			return;
		UserToken userToken = list.get(0);
		userToken.setCreateTime(new Date());
		userTokenDao.update(userToken);

	}

}
