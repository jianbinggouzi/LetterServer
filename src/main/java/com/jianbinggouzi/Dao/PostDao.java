package com.jianbinggouzi.Dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.jianbinggouzi.Domain.Post;

@Repository
public class PostDao extends BaseDao<Post> {

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
		List<Post> list = queryByMapWithOrder(map, "createTime", "desc", pageNo, pageSize);
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

}
