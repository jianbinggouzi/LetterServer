package com.jianbinggouzi.Domain;

import java.util.Date;

import com.jianbinggouzi.Config.EntityClass;

//内容
public class Post extends TextEntityBaseDomain {
	// 内容
	private String postText;
	// 上一条postId 用于一级评论的子评论时
	private String lastPostId;
	// 一级评论/子评论所属于的主Letter/Dynamics的id
	private String letterOrDynamicsId;
	// 一级评论/子评论所属于的具体类型是Letter/Dynamics的
	private EntityClass letterOrDynamics;

	public Post() {
		super();
	}

	public Post(User fromUser, Date createTime, Integer digestNum, Integer replyNum, Integer viewsNum, String postText,
			String lastPostId, String letterOrDynamicsId, EntityClass letterOrDynamics) {
		super(fromUser, createTime, digestNum, replyNum, viewsNum);
		this.postText = postText;
		this.lastPostId = lastPostId;
		this.letterOrDynamicsId = letterOrDynamicsId;
		this.letterOrDynamics = letterOrDynamics;
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

	public String getLetterOrDynamicsId() {
		return letterOrDynamicsId;
	}

	public void setLetterOrDynamicsId(String letterOrDynamicsId) {
		this.letterOrDynamicsId = letterOrDynamicsId;
	}

	public EntityClass getLetterOrDynamics() {
		return letterOrDynamics;
	}

	public void setLetterOrDynamics(EntityClass letterOrDynamics) {
		this.letterOrDynamics = letterOrDynamics;
	}

}
