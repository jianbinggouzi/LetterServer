package com.jianbinggouzi.Dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.jianbinggouzi.Config.EntityClass;
import com.jianbinggouzi.Config.OperateType;
import com.jianbinggouzi.Domain.Dynamics;
import com.jianbinggouzi.Domain.Letter;
import com.jianbinggouzi.Domain.OperateLog;
import com.jianbinggouzi.Domain.Post;
import com.jianbinggouzi.Domain.TextEntityBaseDomain;
import com.jianbinggouzi.Domain.User;

@Repository
public class OperateLogDao extends BaseDao<OperateLog> {

	public OperateLogDao() {
		super();
		this.setTableName("t_operatorLog");
	}

	/**
	 * 获取用户所有发出的操作记录
	 * 
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<OperateLog> getAllSendoutByUser(User user, int pageNo, int pageSize) {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("senderUser", user.getEntityId());
		List<OperateLog> list = queryByMapWithOrder(hashMap, "createTime", "desc", pageNo, pageSize);
		return list;
	}

	/**
	 * 获取用户所有收到的操作记录
	 * 
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<OperateLog> getAllReceiveByUser(User user, int pageNo, int pageSize) {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("receiveUser", user.getEntityId());
		List<OperateLog> list = queryByMapWithOrder(hashMap, "createTime", "desc", pageNo, pageSize);
		return list;
	}

	/**
	 * 设置消息是否被查看
	 * 
	 * @param entityId
	 * @param check
	 */
	public void setIfCheck(String entityId, boolean check) {
		OperateLog operateLog = this.get(entityId);
		operateLog.setIfChecked(check);
		this.update(operateLog);
	}

	/**
	 * 添加或更新登录记录
	 * 
	 * @param user
	 */
	public void addLoginLog(User user) {
		Date createTime = new Date();
		OperateLog operateLog = new OperateLog(user, user, OperateType.LOGIN, createTime, null, null, false);
		saveOrUpdate(operateLog);
	}

	/**
	 * 获取最近的登录记录
	 * 
	 * @param user
	 * @return
	 */
	public OperateLog getLastLoginLog(User user) {
		List<OperateLog> logs = this.find(
				"from " + tableName + " where senderUser = ? and receiveUser = ? order by createTime desc",
				new Object[] { user.getEntityId(), user.getEntityId() });
		for (OperateLog operateLog : logs) {
			if (operateLog.getOperateType() == OperateType.LOGIN) {
				return operateLog;
			}
		}

		return null;
	}

	/**
	 * 根据参数返回EntityClass
	 * 
	 * @param textEntityBaseDomain
	 * @return
	 */
	public EntityClass dispatchEntityClass(TextEntityBaseDomain textEntityBaseDomain) {
		if (textEntityBaseDomain instanceof Letter)
			return EntityClass.LETTER;
		else if (textEntityBaseDomain instanceof Dynamics)
			return EntityClass.DYNAMICS;
		else
			return EntityClass.POST;
	}

	/**
	 * 添加发送Letter/Dynamics记录
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param domain
	 */
	public void addSendLetterOrDynamicsLog(User fromUser, User toUser, TextEntityBaseDomain domain) {
		OperateLog operateLog = new OperateLog(fromUser, null,
				(domain instanceof Letter) ? OperateType.SEND_LETTER : OperateType.SEND_DYNAMICS,
				domain.getCreateTime(), dispatchEntityClass(domain), (Post) domain, false);
		save(operateLog);
	}

	/**
	 * 添加更新Letter/Dynamics记录
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param domain
	 * @return 记录实例的主键
	 */
	public String addUpdateLetterOrDynamicsLog(User fromUser, User toUser, TextEntityBaseDomain domain) {
		OperateLog operateLog = new OperateLog(fromUser, null,
				(domain instanceof Letter) ? OperateType.UPDATE_LETTER : OperateType.UPDATE_DYNAMICS,
				domain.getCreateTime(), dispatchEntityClass(domain), domain, false);
		return (String) save(operateLog);
	}

	/**
	 * 添加点赞记录 点赞时保存的目标id为被赞的主体的id
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param entityClass
	 * @param id
	 * @param target
	 */
	public String addDigestLog(User fromUser, TextEntityBaseDomain target) {
		OperateLog operateLog = new OperateLog(fromUser, target.getFromUser(), OperateType.DIGEST, new Date(),
				dispatchEntityClass(target), target, false);
		return (String) save(operateLog);
	}

	/**
	 * 删除用户相应的记录
	 * 
	 * @param fromUser
	 * @param target
	 * @param operateType
	 *            操作类型
	 * @return 已经删除的id
	 */
	public String deleteOperateLog(User fromUser, TextEntityBaseDomain target, OperateType operateType) {

		HashMap<String, String> map = new HashMap<>();
		map.put("senderUser", fromUser.getEntityId());
		map.put("logid", target.getEntityId());
		List<OperateLog> list = this.queryByMap(map);
		for (OperateLog operateLog : list) {
			if (operateLog.getOperateType() == operateType) {
				String primaryKey = operateLog.getEntityId();
				delete(operateLog);
				return primaryKey;
			}
		}
		return null;
	}

	/**
	 * 添加评论记录 评论时保存的目标id为评论本身的id
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param post
	 * @param target
	 */
	public String addCommentLog(User fromUser, User toUser, Post post) {

		OperateLog operateLog = new OperateLog(fromUser, toUser, OperateType.COMMENT, new Date(), EntityClass.POST,
				post, false);

		return (String) save(operateLog);
	}

	/**
	 * 添加感谢记录 只对Letter
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param entityClass
	 * @param id
	 */
	public String addThankLog(User fromUser, User toUser, Letter letter) {
		OperateLog operateLog = new OperateLog(fromUser, toUser, OperateType.THANKS, new Date(),
				dispatchEntityClass(letter), letter, false);
		return (String) save(operateLog);
	}

	/**
	 * 添加收藏记录 只对letter
	 * 
	 * @param fromUser
	 * @param toUser
	 * @param entityClass
	 * @param id
	 */
	public String addCollectLog(User fromUser, User toUser, Letter letter) {
		OperateLog operateLog = new OperateLog(fromUser, toUser, OperateType.COLLECTS, new Date(),
				dispatchEntityClass(letter), letter, false);
		return (String) save(operateLog);
	}

}
