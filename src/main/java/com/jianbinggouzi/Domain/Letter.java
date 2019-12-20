package com.jianbinggouzi.Domain;

import java.util.Date;

//信件
public class Letter extends TextEntityBaseDomain {
	// 标题
	private String letterTitle;
	// 收藏数
	private Integer collectNums;
	// 内容帖
	private Post mainPost;
	// 感谢数
	private Integer thanksNums;

	public Letter() {
		super();
	}

	public Letter(User fromUser, Date createTime, Integer digestNum, Integer replyNum, Integer viewsNum,
			String letterTitle, Integer collectNums, Post mainPost, Integer thanksNums) {
		super(fromUser, createTime, digestNum, replyNum, viewsNum);
		this.letterTitle = letterTitle;
		this.collectNums = collectNums;
		this.mainPost = mainPost;
		this.thanksNums = thanksNums;
	}

	public String getLetterTitle() {
		return letterTitle;
	}

	public void setLetterTitle(String letterTitle) {
		this.letterTitle = letterTitle;
	}

	public Integer getCollectNums() {
		return collectNums;
	}

	public void setCollectNums(Integer collectNums) {
		this.collectNums = collectNums;
	}

	public Post getMainPost() {
		return mainPost;
	}

	public void setMainPost(Post mainPost) {
		this.mainPost = mainPost;
	}

	public Integer getThanksNums() {
		return thanksNums;
	}

	public void setThanksNums(Integer thanksNums) {
		this.thanksNums = thanksNums;
	}

}
