package com.jianbinggouzi.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jianbinggouzi.Config.OperateType;
import com.jianbinggouzi.Dao.DynamicsDao;
import com.jianbinggouzi.Dao.FollowRelationDao;
import com.jianbinggouzi.Dao.LetterDao;
import com.jianbinggouzi.Dao.OperateLogDao;
import com.jianbinggouzi.Dao.PostDao;
import com.jianbinggouzi.Dao.TextEntityDao;
import com.jianbinggouzi.Dao.UserDao;
import com.jianbinggouzi.Domain.Letter;
import com.jianbinggouzi.Domain.OperateLog;
import com.jianbinggouzi.Domain.Post;
import com.jianbinggouzi.Domain.TextEntityBaseDomain;
import com.jianbinggouzi.Domain.User;

@Service
public class OperateService extends BaseService {

	private UserDao userDao;

	private OperateLogDao operateLogDao;

	private LetterDao letterDao;

	private DynamicsDao dynamicsDao;

	private PostDao postDao;

	private FollowRelationDao followRelationDao;

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setOperateLogDao(OperateLogDao operateLogDao) {
		this.operateLogDao = operateLogDao;
	}

	@Autowired
	public void setLetterDao(LetterDao letterDao) {
		this.letterDao = letterDao;
	}

	@Autowired
	public void setDynamicsDao(DynamicsDao dynamicsDao) {
		this.dynamicsDao = dynamicsDao;
	}

	@Autowired
	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	@Autowired
	public void setFollowRelationDao(FollowRelationDao followRelationDao) {
		this.followRelationDao = followRelationDao;
	}

	/**
	 * 关注用户
	 * 
	 * @param fromUser
	 * @param toUserId
	 * @return
	 */
	public String followUser(User fromUser, String toUserId) {
		User toUser = this.userDao.get(toUserId);
		if (fromUser.getEntityId().equals(toUserId))
			return null;
		return this.followRelationDao.followEntity(fromUser, dispatchEntityClass(toUser), toUser);
	}

	/**
	 * 取消关注
	 * 
	 * @param fromUser
	 * @param entityId
	 */
	public void unFollow(User fromUser, String entityId) {
		followRelationDao.unFollowEntity(fromUser, entityId);
	}

	/**
	 * 设置已读
	 * 
	 * @param entityId
	 */
	public void setChecked(String entityId) {
		this.operateLogDao.setIfCheck(entityId, true);
	}

	/**
	 * 设置未读
	 * 
	 * @param entityId
	 */
	public void setUnchecked(String entityId) {
		this.operateLogDao.setIfCheck(entityId, false);
	}

	/**
	 * 获取用户所有发出的操作类型
	 * 
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<OperateLog> getAllSendoutByUser(User user, int pageNo, int pageSize) {
		List<OperateLog> list = this.operateLogDao.getAllSendoutByUser(user, pageNo, pageSize);

		for (OperateLog operateLog : list) {
			switch (operateLog.getEntityClass()) {
			case LETTER:
				operateLog.setLogId(letterDao.get(operateLog.getLogId().getEntityId()));
				break;
			case DYNAMICS:
				operateLog.setLogId(dynamicsDao.get(operateLog.getLogId().getEntityId()));
				break;
			case POST:
				operateLog.setLogId(postDao.get(operateLog.getLogId().getEntityId()));
				break;
			}
		}

		return list;
	}

	/**
	 * 获取用户所有收到的操作类型
	 * 
	 * @param user
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<OperateLog> getAllReceiveByUser(User user, int pageNo, int pageSize) {
		List<OperateLog> list = this.operateLogDao.getAllReceiveByUser(user, pageNo, pageSize);

		for (OperateLog operateLog : list) {
			switch (operateLog.getEntityClass()) {
			case LETTER:
				operateLog.setLogId(letterDao.get(operateLog.getLogId().getEntityId()));
				break;
			case DYNAMICS:
				operateLog.setLogId(dynamicsDao.get(operateLog.getLogId().getEntityId()));
				break;
			case POST:
				operateLog.setLogId(postDao.get(operateLog.getLogId().getEntityId()));
				break;
			}
		}

		return list;
	}

	/**
	 * 点赞
	 * 
	 * @param user
	 * @param type
	 *            letter/dynamics/post
	 * @param entityId
	 * @return
	 */
	public String digestToTextEntity(User user, String type, String entityId) {

		// 点赞数加一
		TextEntityDao textEntityDao = dispatchDaoByClassName(type);
		textEntityDao.incDigestNum(entityId);

		// 添加记录
		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(entityId);
		return operateLogDao.addDigestLog(user, textDomain);
	}

	/**
	 * 取消点赞 如果指定的id不存在，直接抛出异常
	 * 
	 * @param user
	 * @param type
	 *            letter/dynamics/post
	 * @param digestedId
	 * @return 已经删除的记录id
	 * @throws Exception
	 */
	public String cancelDigestToTextEntity(User user, String type, String digestedId) throws Exception {

		TextEntityDao textEntityDao = dispatchDaoByClassName(type);
		textEntityDao.decDigestNum(digestedId);

		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(digestedId);
		return operateLogDao.deleteOperateLog(user, textDomain, OperateType.DIGEST);

	}

	/**
	 * 添加评论
	 * 
	 * @param user
	 * @param type
	 *            letter/dynamics/post
	 * @param entityId
	 * @param text
	 * @param commentedId
	 * @return 记录的id
	 */
	public String commentToTextEntity(User user, String type, String text, String commentedId) {
		// 回复数加一
		TextEntityDao textEntityDao = dispatchDaoByClassName(type);
		textEntityDao.incReplayNum(commentedId);

		// 添加评论
		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(commentedId);
		String postKey = postDao.addPostOfEntity(user, text, textDomain);

		// 添加记录
		Post post = (Post) postDao.get(postKey);
		return operateLogDao.addCommentLog(user, textDomain.getFromUser(), post);
	}

	/**
	 * 删除评论 用户不匹配的情况下抛出异常并终止
	 * 
	 * @param user
	 * @param type
	 *            letter/dynamics/post
	 * @param commentId
	 * @throws Exception
	 */
	public String deleteComment(User user, String type, String commentId) throws Exception {

		// 被回复的实体回复数减一
		TextEntityDao textEntityDao = dispatchDaoByClassName(type);
		textEntityDao.decReplayNum(commentId);

		// 删除评论实体
		Post post = postDao.get(commentId);
		postDao.delete(post);
		// 删除记录
		return operateLogDao.deleteOperateLog(user, post, OperateType.COMMENT);
	}

	/**
	 * 感谢 只对Letter
	 * 
	 * @param user
	 * @param letterId
	 * @return 记录的id
	 */
	public String thanksToLetter(User user, String letterId) {
		// 感谢数加一
		letterDao.incThanksNum(letterId);

		// 保存记录
		Letter letter = letterDao.get(letterId);
		return operateLogDao.addThankLog(user, letter.getFromUser(), letter);
	}

	/**
	 * 取消感谢
	 * 
	 * @param user
	 * @param letterId
	 * @throws Exception
	 */
	public String cancelThanksToLetter(User user, String letterId) throws Exception {

		// 更新Letter实体
		letterDao.decThanksNum(letterId);

		Letter letter = letterDao.get(letterId);
		return operateLogDao.deleteOperateLog(user, letter, OperateType.THANKS);

	}

	/**
	 * 收藏 只对Letter
	 * 
	 * @param user
	 * @param letterId
	 * @return 记录的id
	 */
	public String colletctToLetter(User user, String letterId) {

		letterDao.incCollectNum(letterId);

		// 保存记录
		Letter letter = letterDao.get(letterId);
		return operateLogDao.addCollectLog(user, user, letter);
	}

	/**
	 * 取消收藏
	 * 
	 * @param user
	 * @param letterId
	 */
	public String cancelCollectToLetter(User user, String letterId) {

		letterDao.deccCollectNum(letterId);

		// 更新Letter实体
		Letter letter = letterDao.get(letterId);
		return operateLogDao.deleteOperateLog(user, letter, OperateType.COLLECTS);
	}

}
