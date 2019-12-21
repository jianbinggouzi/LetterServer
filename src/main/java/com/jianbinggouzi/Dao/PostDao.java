package com.jianbinggouzi.Dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.jianbinggouzi.Config.EntityClass;
import com.jianbinggouzi.Domain.Dynamics;
import com.jianbinggouzi.Domain.Letter;
import com.jianbinggouzi.Domain.Post;
import com.jianbinggouzi.Domain.TextEntityBaseDomain;
import com.jianbinggouzi.Domain.User;

@Repository
public class PostDao extends TextEntityDao<Post> {

	public PostDao() {
		super();
		this.setTableName("t_post");
	}

	/**
	 * 获得lastPostId为指定id的子Posts以及子Post递归得到的所有子Post
	 * 
	 * @param lastId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Post> getPostsByLastId(String lastId, int pageNo, int pageSize) {
		HashMap<String, String> map = new HashMap<>();
		map.put("lastPostId", lastId);
		List<Post> list = queryByMapAndWithOrder(map, "createTime", "desc", pageNo, pageSize);
		if (!(list == null || list.size() == 0))
			list.addAll(getSecondPostsByLastId(list));
		return list;
	}

	/**
	 * 递归获取所有子Post
	 * 
	 * @param list
	 * @return
	 */
	public List<Post> getSecondPostsByLastId(List<Post> list) {
		if (list == null || list.size() == 0) {
			return null;
		}
		List<Post> result = new ArrayList<Post>();
		HashMap<String, String> map = new HashMap<>();
		for (Post post : list) {
			map.clear();
			map.put("lastPostId", post.getEntityId());
			List<Post> temp = queryByMap(map);
			if (!(temp == null || temp.size() == 0))
				result.addAll(temp);
		}
		if (!(result == null || result.size() == 0))
			result.addAll(getSecondPostsByLastId(result));
		return result;

	}

	/**
	 * 添加对主体的回复
	 * 
	 * @param user
	 * @param type
	 * @param text
	 * @param textDomain
	 * @return 保存的主键
	 */
	public String addPostOfEntity(User user, String text, TextEntityBaseDomain textDomain) {
		Post post = null;
		String commentedId = textDomain.getEntityId();
		if (textDomain instanceof Letter) {
			post = new Post(user, new Date(), 0, 0, 0, text, commentedId, commentedId, EntityClass.LETTER);
		} else if (textDomain instanceof Dynamics) {
			post = new Post(user, new Date(), 0, 0, 0, text, commentedId, commentedId, EntityClass.DYNAMICS);
		} else if (textDomain instanceof Post) {
			post = new Post(user, new Date(), 0, 0, 0, text, commentedId, ((Post) textDomain).getLetterOrDynamicsId(),
					((Post) textDomain).getLetterOrDynamics());
		}

		// 保存内容
		return (String) save(post);
	}

}
