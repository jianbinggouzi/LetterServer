package com.jianbinggouzi.Service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jianbinggouzi.Config.OperateType;
import com.jianbinggouzi.Dao.BaseDao;
import com.jianbinggouzi.Dao.DynamicsDao;
import com.jianbinggouzi.Dao.LetterDao;
import com.jianbinggouzi.Dao.OperateLogDao;
import com.jianbinggouzi.Dao.PostDao;
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
		BaseDao textEntityDao = dispatchDaoByClassName(type);
		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(entityId);
		textDomain.setDigestNum(textDomain.getDigestNum() + 1);
		textEntityDao.update(textDomain);

		// 添加记录
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

		BaseDao textEntityDao = dispatchDaoByClassName(type);
		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(digestedId);
		textDomain.setDigestNum(textDomain.getDigestNum() - 1);
		textEntityDao.update(textDomain);

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
		BaseDao textEntityDao = dispatchDaoByClassName(type);
		if (textEntityDao == null)
			System.out.print("error");
		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(commentedId);
		textDomain.setReplyNum(textDomain.getReplyNum() + 1);
		textEntityDao.update(textDomain);

		// 保存内容
		Post mainPost = new Post(user, new Date(), 0, 0, 0, text, commentedId);
		postDao.save(mainPost);

		// 添加记录
		return operateLogDao.addCommentLog(user, textDomain.getFromUser(), mainPost);
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

		Post post = postDao.get(commentId);

		// 被回复的实体回复数减一
		BaseDao textEntityDao = dispatchDaoByClassName(type);
		TextEntityBaseDomain textDomain = (TextEntityBaseDomain) textEntityDao.get(post.getLastPostId());
		textDomain.setReplyNum(textDomain.getReplyNum() - 1);
		textEntityDao.update(textDomain);

		// 删除评论实体
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
		Letter letter = letterDao.get(letterId);
		letter.setThanksNums(letter.getThanksNums() + 1);
		letterDao.update(letter);

		// 保存记录
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
		Letter letter = letterDao.get(letterId);
		letter.setThanksNums(letter.getThanksNums() - 1);
		letterDao.update(letter);

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
		Letter letter = letterDao.get(letterId);
		letter.setCollectNums(letter.getCollectNums() + 1);
		letterDao.update(letter);
		// 保存记录
		return operateLogDao.addCollectLog(user, user, letter);
	}

	/**
	 * 取消收藏
	 * 
	 * @param user
	 * @param letterId
	 */
	public String cancelCollectToLetter(User user, String letterId) {

		// 更新Letter实体
		Letter letter = letterDao.get(letterId);
		letter.setCollectNums(letter.getCollectNums() - 1);
		letterDao.update(letter);

		return operateLogDao.deleteOperateLog(user, letter, OperateType.COLLECTS);
	}

}
