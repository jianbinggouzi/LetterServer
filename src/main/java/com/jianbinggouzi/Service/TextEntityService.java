package com.jianbinggouzi.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jianbinggouzi.Dao.BaseDao;
import com.jianbinggouzi.Dao.DynamicsDao;
import com.jianbinggouzi.Dao.LetterDao;
import com.jianbinggouzi.Dao.OperateLogDao;
import com.jianbinggouzi.Dao.PostDao;
import com.jianbinggouzi.Dao.UserDao;
import com.jianbinggouzi.Domain.Dynamics;
import com.jianbinggouzi.Domain.Letter;
import com.jianbinggouzi.Domain.Post;
import com.jianbinggouzi.Domain.TextEntityBaseDomain;
import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Exception.ExceptionWithMessage;

@Service
public class TextEntityService extends BaseService {

	private LetterDao letterDao;

	private DynamicsDao dynamicsDao;

	private PostDao postDao;

	private UserDao userDao;

	private OperateLogDao operateLogDao;

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
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setOperateLogDao(OperateLogDao operateLogDao) {
		this.operateLogDao = operateLogDao;
	}

	/**
	 * 添加Letter/Dynamics实例 用于用户发表动态 信件，并且增加积分
	 * 
	 * @param fromUser
	 * @param createTime
	 * @param letterTitle
	 * @param postText
	 * @return
	 */
	public String addLetterOrDynamics(User fromUser, String letterTitle, String postText) {
		// 增加积分
		fromUser = userDao.get(fromUser.getEntityId());
		fromUser.setUserCredit(fromUser.getUserCredit() + 100);
		userDao.update(fromUser);

		// 实例化对象 如果是Letter，还需要设置题目
		Date createTime = new Date();
		TextEntityBaseDomain domain;
		Post mainPost = new Post(fromUser, createTime, 0, 0, 0, postText, null);

		if (letterTitle != null) {
			domain = new Letter(fromUser, createTime, 0, 0, 0, letterTitle, 0, mainPost, 0);
		} else {
			domain = new Dynamics(fromUser, createTime, 0, 0, 0, mainPost);
		}

		// 保存实例
		BaseDao dao = dispatchDao(domain);
		String primaryKey = (String) dao.save(domain);

		operateLogDao.addSendLetterOrDynamicsLog(fromUser, null, domain);

		return primaryKey;
	}

	/**
	 * 更新Letter/Dynamics实例 用于用户修改信件 动态
	 * 
	 * @param fromUser
	 * @param createTime
	 * @param letterTitle
	 * @param postText
	 * @throws ExceptionWithMessage
	 */
	public String updateLetterOrDynamics(User fromUser, String letterTitle, String postText, String entityId)
			throws ExceptionWithMessage {

		// 获取实例 如果是Letter，还需要更新题目
		TextEntityBaseDomain domain;
		Post mainPost;
		if (letterTitle != null) {
			domain = letterDao.get(entityId);
			mainPost = ((Letter) domain).getMainPost();
			((Letter) domain).setLetterTitle(letterTitle);
		} else {
			domain = dynamicsDao.get(entityId);
			mainPost = ((Dynamics) domain).getMainPost();
		}
		// 是否是同一人修改
		if (!domain.getFromUser().getEntityId().equals(fromUser.getEntityId())) {
			throw new ExceptionWithMessage("没有权限");
		}

		mainPost.setPostText(postText);
		postDao.update(mainPost);

		// 保存实例
		BaseDao dao = dispatchDao(domain);
		dao.update(domain);

		// 设置记录为更新时的时间
		domain.setCreateTime(new Date());
		return operateLogDao.addUpdateLetterOrDynamicsLog(fromUser, null, domain);
	}

	/**
	 * 获得指定Post递归得到的子Post 用于获取评论
	 * 
	 * @param lastId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Post> getAllPostsByLastId(String lastId, int pageNo, int pageSize) {
		return postDao.getPostsByLastId(lastId, pageNo, pageSize);
	}

	/**
	 * 分页查询指定用户的所有TextEnyityDomain 按照创建时间降续排序 用于获取用户所有发出的信件 动态 评论
	 * 
	 * @param user
	 * @param type
	 *            letter/dynamics/post
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ExceptionWithMessage
	 */
	public List<TextEntityBaseDomain> getAllTextFromUserByTime(User user, String type, int pageNo, int pageSize)
			throws ExceptionWithMessage {
		type = type.toLowerCase();
		BaseDao textEntityDao = dispatchDaoByClassName(type);

		Map<String, String> args = new HashMap<String, String>();
		args.put("fromUser", user.getEntityId());

		List<TextEntityBaseDomain> textDomainList = textEntityDao.queryByMapWithOrder(args, "createTime", "desc",
				pageNo, pageSize);
		return textDomainList;
	}

	/**
	 * 分页查询所有的letter/dynamics 用于首页展示信件 动态
	 * 
	 * @param user
	 * @param type
	 *            letter/dynamics
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws ExceptionWithMessage
	 */
	public List<TextEntityBaseDomain> getAllLetterOrDynamics(String type, int pageNo, int pageSize)
			throws ExceptionWithMessage {
		type = type.toLowerCase();
		if (type.equals("post"))
			return null;

		BaseDao textEntityDao = dispatchDaoByClassName(type);

		List<TextEntityBaseDomain> list = textEntityDao.queryByMapWithOrder(null, "createTime", "desc", pageNo,
				pageSize);

		return list;
	}

}
