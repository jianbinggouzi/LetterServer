package com.jianbinggouzi.Dao;

import org.springframework.stereotype.Repository;

import com.jianbinggouzi.Domain.Letter;

@Repository
public class LetterDao extends BaseDao<Letter> {

	public LetterDao() {
		super();
		setTableName("t_letter");
	}
}
