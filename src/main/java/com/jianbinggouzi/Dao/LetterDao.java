package com.jianbinggouzi.Dao;

import org.springframework.stereotype.Repository;

import com.jianbinggouzi.Domain.Letter;

@Repository
public class LetterDao extends TextEntityDao<Letter> {

	public LetterDao() {
		super();
		setTableName("t_letter");
	}

	/**
	 * 加1感谢数
	 * 
	 * @param letterId
	 */
	public void incThanksNum(String letterId) {
		Letter letter = get(letterId);
		letter.setThanksNums(letter.getThanksNums() + 1);
		update(letter);
	}

	/**
	 * 减1感谢数
	 * 
	 * @param letterId
	 */
	public void decThanksNum(String letterId) {
		Letter letter = get(letterId);
		letter.setThanksNums(letter.getThanksNums() - 1);
		update(letter);
	}

	/**
	 * 加1收藏数
	 * 
	 * @param letterId
	 */
	public void incCollectNum(String letterId) {
		Letter letter = get(letterId);
		letter.setCollectNums(letter.getCollectNums() + 1);
		update(letter);
	}

	/**
	 * 减1收藏数
	 * 
	 * @param letterId
	 */
	public void deccCollectNum(String letterId) {
		Letter letter = get(letterId);
		letter.setCollectNums(letter.getCollectNums() - 1);
		update(letter);
	}
}
