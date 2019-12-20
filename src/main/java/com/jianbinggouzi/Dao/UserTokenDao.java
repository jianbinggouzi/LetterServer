package com.jianbinggouzi.Dao;

import org.springframework.stereotype.Repository;

import com.jianbinggouzi.Domain.UserToken;

@Repository
public class UserTokenDao extends BaseDao<UserToken> {
	public UserTokenDao() {
		setTableName("t_userToken");
	}

}
