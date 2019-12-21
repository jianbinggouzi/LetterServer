package com.jianbinggouzi.Web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jianbinggouzi.Domain.OperateLog;
import com.jianbinggouzi.Domain.Post;
import com.jianbinggouzi.Domain.User;
import com.jianbinggouzi.Service.OperateService;
import com.jianbinggouzi.Service.TextEntityService;

@Controller
@RequestMapping("/operate")
public class OperateController extends BaseController {

	private OperateService operateService;

	private TextEntityService textEntityService;

	@Autowired
	public void setOperateService(OperateService operateService) {
		this.operateService = operateService;
	}

	@Autowired
	public void setTextEntityService(TextEntityService textEntityService) {
		this.textEntityService = textEntityService;
	}

	/**
	 * 获取用户所有发出的消息
	 * 
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/getAllSendout")
	@ResponseBody
	public List<OperateLog> getAllSendout(String token, Integer pageNo, Integer pageSize) {
		List<OperateLog> list = operateService.getAllSendoutByUser(getUser(token), pageNo, pageSize);
		List<Post> res = new ArrayList<Post>();
		for (OperateLog log : list) {
			try {
				res.add((Post) log.getLogId());
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return list;
	}

	/**
	 * 获取用户所有收到的消息
	 * 
	 * @param token
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/getAllReceive")
	@ResponseBody
	public List<OperateLog> getAllReceive(String token, Integer pageNo, Integer pageSize) {
		List<OperateLog> list = operateService.getAllReceiveByUser(getUser(token), pageNo, pageSize);
		List<Post> res = new ArrayList<Post>();
		for (OperateLog log : list) {
			try {
				res.add((Post) log.getLogId());
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		return list;
	}

	/**
	 * 设置消息已读
	 * 
	 * @param entityId
	 */
	@RequestMapping("/setCheck")
	@ResponseBody
	public void setCheck(String entityId) {
		operateService.setChecked(entityId);
	}

	/**
	 * 设置消息未读
	 * 
	 * @param entityId
	 */
	@RequestMapping("/setUncheck")
	@ResponseBody
	public void setUncheck(String entityId) {
		operateService.setUnchecked(entityId);
	}

	/**
	 * 关注用户
	 * 
	 * @param token
	 * @param userId
	 * @return
	 */
	@RequestMapping("/followUser")
	@ResponseBody
	public HashMap<String, String> followUser(String token, String userId) {
		HashMap<String, String> map = new HashMap<>();
		map.put("result", this.operateService.followUser(getUser(token), userId));
		return map;
	}

	/**
	 * 取消关注用户
	 * 
	 * @param token
	 * @param userId
	 */
	public HashMap<String, String> unfollowUser(String token, String userId) {
		HashMap<String, String> map = new HashMap<>();
		try {
			this.operateService.unFollow(getUser(token), userId);
			map.put("result", "success");
		} catch (Exception e) {
			map.put("result", "failed");
		} finally {
			return map;
		}

	}

	/**
	 * 点赞
	 * 
	 * @param user
	 * @param type
	 * @param entityId
	 * @return 记录的主键
	 */
	@RequestMapping("/digest")
	public String digest(User user, String type, String entityId) {
		return operateService.digestToTextEntity(user, type, entityId);
	}

	/**
	 * 取消点赞
	 * 
	 * @param user
	 * @param type
	 * @param entityId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cancelDigest")
	public String undigest(String token, String type, String entityId) throws Exception {
		User user = getUser(token);
		return operateService.cancelDigestToTextEntity(user, type, entityId);
	}

	/**
	 * 添加评论
	 * 
	 * @param token
	 * @param type
	 *            letter/dynamics/post
	 * @param entityId
	 * @param text
	 * @param lastPostId
	 * @return
	 */
	@RequestMapping("/comment")
	@ResponseBody
	public HashMap<String, String> comment(String token, String type, String entityId, String text, String lastPostId) {
		User user = getUser(token);
		HashMap<String, String> map = new HashMap<>();
		map.put("result", operateService.commentToTextEntity(user, type, text, lastPostId));
		return map;
	}

	/**
	 * 删除评论
	 * 
	 * @param request
	 * @param type
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/deleteComment")
	public String deletecomment(String token, String type, String commentId) throws Exception {
		User user = getUser(token);
		return operateService.deleteComment(user, type, commentId);
	}

	/**
	 * 获取实体的评论
	 * 
	 * @param entityId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/getComments")
	@ResponseBody
	public List<Post> getComments(String entityId, int pageNo, int pageSize) {
		return textEntityService.getAllPostsByLastId(entityId, pageNo, pageSize);
	}

	/**
	 * 感谢 只对letter
	 * 
	 * @param request
	 * @param type
	 * @param letterId
	 * @return
	 */
	@RequestMapping("/thanks")
	public String thankToLetter(String token, String letterId) {
		User user = getUser(token);

		return operateService.thanksToLetter(user, letterId);
	}

	/**
	 * 取消感谢 只对Letter
	 * 
	 * @param request
	 * @param letterId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cancelThanks")
	public String cancelThankToLetter(String token, String letterId) throws Exception {
		User user = getUser(token);
		return operateService.cancelThanksToLetter(user, letterId);
	}

	/**
	 * 收藏letter
	 * 
	 * @param request
	 * @param letterId
	 * @return
	 */
	@RequestMapping("/collect")
	public String collectToLetter(String token, String letterId) {
		User user = getUser(token);
		return operateService.colletctToLetter(user, letterId);
	}

	/**
	 * 取消收藏letter
	 * 
	 * @param request
	 * @param letterId
	 * @return
	 */
	@RequestMapping("/cancelCollect")
	public String cancelCollectToLetter(String token, String letterId) {
		User user = getUser(token);
		return operateService.cancelCollectToLetter(user, letterId);
	}
}
