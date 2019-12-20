package com.jianbinggouzi.Domain;

import java.io.Serializable;

//所有实体类的基础类
public class EntityBaseDomain implements Serializable {
	// 主键
	private String entityId;

	public EntityBaseDomain() {

	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

}
