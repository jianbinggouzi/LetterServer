package com.jianbinggouzi.Domain;

import java.util.Date;

//内容
public class Post extends TextEntityBaseDomain {
	// 内容
	private String postText;
	// 上一条postId 用于评论时
	private String lastPostId;

	public Post() {
		super();
	}

	public Post(User fromUser, Date createTime, Integer digestNum, Integer replyNum, Integer viewsNum, String postText,
			String lastPostId) {
		super(fromUser, createTime, digestNum, replyNum, viewsNum);
		this.postText = postText;
		this.lastPostId = lastPostId;
	}

	public String getPostText() {
		return postText;
	}

	public void setPostText(String postText) {
		this.postText = postText;
	}

	public String getLastPostId() {
		return lastPostId;
	}

	public void setLastPostId(String lastPostId) {
		this.lastPostId = lastPostId;
	}

}
