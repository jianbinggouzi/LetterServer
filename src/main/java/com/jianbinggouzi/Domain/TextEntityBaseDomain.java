package com.jianbinggouzi.Domain;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

//信件、动态、内容的基础类
public class TextEntityBaseDomain extends EntityBaseDomain {
	// 发出者 多对一关系
	private User fromUser;
	// 创建时间
	private Date createTime;
	// 点赞数
	private Integer digestNum;
	// 评论/回复数
	private Integer replyNum;
	// 查看次数
	private Integer viewsNum;
	// 所有评论 只在运行时使用 数据库不进行映射
	private CopyOnWriteArrayList<String> commentsList;
	// 所有点赞人 只在运行时使用 数据库不进行映射
	private CopyOnWriteArrayList<User> digestsUserLists;

	public TextEntityBaseDomain() {
		super();
	}

	public TextEntityBaseDomain(User fromUser, Date createTime, Integer digestNum, Integer replyNum, Integer viewsNum) {
		super();
		this.fromUser = fromUser;
		this.createTime = createTime;
		this.digestNum = digestNum;
		this.replyNum = replyNum;
		this.viewsNum = viewsNum;
		this.commentsList = new CopyOnWriteArrayList<>();
		this.digestsUserLists = new CopyOnWriteArrayList<>();
	}

	public TextEntityBaseDomain(User fromUser, Date createTime, Integer digestNum, Integer replyNum, Integer viewsNum,
			CopyOnWriteArrayList<String> commentsList, CopyOnWriteArrayList<User> digestsUserLists) {
		super();
		this.fromUser = fromUser;
		this.createTime = createTime;
		this.digestNum = digestNum;
		this.replyNum = replyNum;
		this.viewsNum = viewsNum;
		this.commentsList = commentsList;
		this.digestsUserLists = digestsUserLists;
	}

	public CopyOnWriteArrayList<User> getDigestsUserLists() {
		return digestsUserLists;
	}

	public void setDigestsUserLists(CopyOnWriteArrayList<User> digestsUserLists) {
		this.digestsUserLists = digestsUserLists;
	}

	public CopyOnWriteArrayList<String> getCommentsList() {
		return commentsList;
	}

	public void setCommentsList(CopyOnWriteArrayList<String> commentsList) {
		this.commentsList = commentsList;
	}

	public User getFromUser() {
		return fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getDigestNum() {
		return digestNum;
	}

	public void setDigestNum(Integer digestNum) {
		this.digestNum = digestNum;
	}

	public Integer getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(Integer replyNum) {
		this.replyNum = replyNum;
	}

	public Integer getViewsNum() {
		return viewsNum;
	}

	public void setViewsNum(Integer viewsNum) {
		this.viewsNum = viewsNum;
	}

}
