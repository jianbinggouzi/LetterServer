package com.jianbinggouzi.Domain;

import java.util.Date;

import com.jianbinggouzi.Config.EntityClass;
import com.jianbinggouzi.Config.OperateType;

//点赞、评论等操作记录
public class OperateLog extends EntityBaseDomain {
	private User senderUser;
	private User receiveUser;
	// 操作类型
	private OperateType operateType;
	private Date createTime;
	// 实体类别，用于定位目标所在表
	private EntityClass entityClass;
	// 目标id
	private TextEntityBaseDomain logId;
	// 是否已经查看
	private Boolean ifChecked;

	public OperateLog() {

	}

	public OperateLog(User sender, User receiver, OperateType type, Date time, EntityClass entityClass,
			TextEntityBaseDomain logId, Boolean ifChecked) {
		this.senderUser = sender;
		this.receiveUser = receiver;
		this.operateType = type;
		this.createTime = time;
		this.entityClass = entityClass;
		this.logId = logId;
		this.ifChecked = ifChecked;
	}

	public User getSenderUser() {
		return senderUser;
	}

	public void setSenderUser(User senderUser) {
		this.senderUser = senderUser;
	}

	public User getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(User receiveUser) {
		this.receiveUser = receiveUser;
	}

	public OperateType getOperateType() {
		return operateType;
	}

	public void setOperateType(OperateType operateType) {
		this.operateType = operateType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public EntityClass getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(EntityClass entityClass) {
		this.entityClass = entityClass;
	}

	public TextEntityBaseDomain getLogId() {
		return logId;
	}

	public void setLogId(TextEntityBaseDomain logId) {
		this.logId = logId;
	}

	public Boolean getIfChecked() {
		return ifChecked;
	}

	public void setIfChecked(Boolean ifChecked) {
		this.ifChecked = ifChecked;
	}

}
