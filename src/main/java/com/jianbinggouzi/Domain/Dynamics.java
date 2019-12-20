package com.jianbinggouzi.Domain;

import java.util.Date;

//用户动态
public class Dynamics extends TextEntityBaseDomain {
	// 主要内容
	private Post mainPost;

	public Dynamics(User fromUser, Date createTime, Integer digestNum, Integer replyNum, Integer viewsNum,
			Post mainPost) {
		super(fromUser, createTime, digestNum, replyNum, viewsNum);
		this.mainPost = mainPost;
	}

	public Post getMainPost() {
		return mainPost;
	}

	public void setMainPost(Post mainPost) {
		this.mainPost = mainPost;
	}
}
