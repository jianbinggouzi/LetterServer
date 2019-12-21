package com.jianbinggouzi.Dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;

import com.jianbinggouzi.Config.Output;

public class BaseDao<T> {

	protected String tableName = "";

	// 对应的实体的类
	protected Class<T> entityClass;

	protected HibernateTemplate hibernateTemplate;

	@Autowired
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
		Output.log("---------------", "hibernateTemplate bean注入成功");
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public BaseDao() {
		Type type = getClass().getGenericSuperclass();
		ParameterizedType params = (ParameterizedType) type;
		entityClass = (Class<T>) (params.getActualTypeArguments()[0]);
		Output.log("---------------", entityClass.toString() + "Dao 加载完成");
	}

	/**
	 * 获取实例
	 * 
	 * @param id
	 * @return
	 */
	public T get(Serializable id) {
		return getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 加载实例
	 * 
	 * @param id
	 * @return
	 */
	public T load(Serializable id) {
		return getHibernateTemplate().load(entityClass, id);
	}

	/**
	 * 保存实例
	 * 
	 * @param entity
	 * @return 主键
	 */
	public Serializable save(T entity) {
		return getHibernateTemplate().save(entity);
	}

	/**
	 * 删除实例
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * 更新或保存实例
	 * 
	 * @param entity
	 */
	public void saveOrUpdate(T entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}

	/**
	 * 更新实例
	 * 
	 * @param entity
	 */
	public void update(T entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * 根据hql查询
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public List<T> find(String hql, Object... params) {
		return (List<T>) (getHibernateTemplate().find(hql, params));
	}

	/**
	 * 根据指定的key-value查询 用于精确查询或结果比较少的查询
	 * 
	 * @param map
	 *            条件-值
	 * @return
	 */
	public List<T> queryByMap(Map<String, String> map) {
		StringBuilder sql = new StringBuilder("select * from " + tableName);

		// 参数的List，用于生成SQLPrepareStatement时使用
		List<Object> args = new ArrayList<Object>();
		// 拼接sql语句的where部分
		if (map != null && map.size() != 0) {
			int i = 0;
			sql.append(" where");
			for (Map.Entry<String, String> entity : map.entrySet()) {
				sql.append(" ").append(entity.getKey()).append("=?");
				sql.append((map.size() == ++i) ? "" : " and");
				System.out.println(entity.getKey() + " " + entity.getValue());
				args.add(entity.getValue());
			}
		}

		final Query query = createSQLQuery(sql.toString(), args);
		// 主键的List
		List list = (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				return query.list();

			}
		});

		return list;

	}

	/**
	 * 根据指定的key-value进行分页查询，其中key-value之间使用and连接 用于大量查询，可以指定排序
	 * 
	 * @param key_value
	 *            条件-值
	 * @param orderKey
	 *            指定order by的变量名
	 * @param order
	 *            指定desc asc
	 * @param pageNo
	 *            从1开始
	 * @param pageSize
	 * @return
	 */
	public List<T> queryByMapAndWithOrder(Map<String, String> key_value, String orderKey, String order, int pageNo,
			int pageSize) {
		// 参数的List 用于生成SQLPreparementStatement
		List<Object> args = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + tableName + " where");
		// 拼接where部分
		if (key_value != null && key_value.size() != 0) {
			for (Map.Entry<String, String> entry : key_value.entrySet()) {
				sql.append(" ").append(entry.getKey()).append("=").append("?");
				sql.append(" and");
				args.add(entry.getValue());
			}

		}

		// desc or asc
		sql.append(" ").append(orderKey).append((order.equals("asc") ? ">=" : "<="));
		// 考虑到分页到后面的效率，用子嵌套查询有索引的方式优化
		sql.append("(select ").append(orderKey).append(" from " + tableName);
		// 拼接子查询的where部分
		if (key_value != null && key_value.size() > 0) {
			sql.append(" where");
			int i = 0;
			for (Map.Entry<String, String> entry : key_value.entrySet()) {

				sql.append(" ").append(entry.getKey()).append("=").append("?");
				args.add(entry.getValue());
				sql.append((key_value.size() == ++i) ? ("") : (" and"));
			}
		}

		sql.append(" ").append("order by ").append(orderKey).append(" ").append(order);
		// 获取到前一页的最后一个元素在数据库中的位置
		int lastIndex = (pageNo - 1) * pageSize;
		// 设置开始位置
		sql.append(" ").append("limit ").append(lastIndex + "").append(",1)");
		sql.append(" ").append("order by ").append(orderKey).append(" ").append(order).append(" limit 0,")
				.append("" + pageSize);

		final Query query = createSQLQuery(sql.toString(), args);
		System.out.println(query.getQueryString());
		// 符合条件的实体的主键list
		List list = (List) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				return query.list();

			}

		});

		return list;

	}

	/**
	 * 根据指定的key-value进行分页查询，其中key-value使用or连接 用于大量查询，可以指定排序
	 * 
	 * @param key_value
	 *            条件-值
	 * @param orderKey
	 *            指定order by的变量名
	 * @param order
	 *            指定desc asc
	 * @param pageNo
	 *            从1开始
	 * @param pageSize
	 * @return
	 */
	public List<T> queryByMapOrWithOrder(Map<String, String> key_value, String orderKey, String order, int pageNo,
			int pageSize) {
		// 参数的List 用于生成SQLPreparementStatement
		List<Object> args = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + tableName + " where");
		// 拼接where部分
		if (key_value != null && key_value.size() != 0) {
			for (Map.Entry<String, String> entry : key_value.entrySet()) {
				sql.append(" ").append(entry.getKey()).append("=").append("?");
				sql.append(" or");
				args.add(entry.getValue());
			}

		}

		// desc or asc
		sql.append(" ").append(orderKey).append((order.equals("asc") ? ">=" : "<="));
		// 考虑到分页到后面的效率，用子嵌套查询有索引的方式优化
		sql.append("(select ").append(orderKey).append(" from " + tableName);
		// 拼接子查询的where部分
		if (key_value != null && key_value.size() > 0) {
			sql.append(" where");
			int i = 0;
			for (Map.Entry<String, String> entry : key_value.entrySet()) {

				sql.append(" ").append(entry.getKey()).append("=").append("?");
				args.add(entry.getValue());
				sql.append((key_value.size() == ++i) ? ("") : (" and"));
			}
		}

		sql.append(" ").append("order by ").append(orderKey).append(" ").append(order);
		// 获取到前一页的最后一个元素在数据库中的位置
		int lastIndex = (pageNo - 1) * pageSize;
		// 设置开始位置
		sql.append(" ").append("limit ").append(lastIndex + "").append(",1)");
		sql.append(" ").append("order by ").append(orderKey).append(" ").append(order).append(" limit 0,")
				.append("" + pageSize);

		final Query query = createSQLQuery(sql.toString(), args);
		System.out.println(query.getQueryString());
		// 符合条件的实体的主键list
		List list = (List) getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				return query.list();

			}

		});

		return list;

	}

	/**
	 * 根据示例查询 不会带主键 不会有级联
	 * 
	 * @param object
	 * @return
	 */
	public List<T> findByExample(Object object) {
		return getHibernateTemplate().findByExample((T) object);
	}

	/**
	 * 获取当前的Session
	 * 
	 * @return
	 */
	public Session getSession() {
		return hibernateTemplate.getSessionFactory().getCurrentSession();
	}

	/**
	 * 创建Query对象，并开启查询缓存
	 * 
	 * @param hql
	 * @param params
	 * @return
	 */
	public Query createSQLQuery(String sql, List<Object> list) {
		Query query = getSession().createSQLQuery(sql).addEntity(entityClass).setCacheable(true);
		for (int i = 0; i < list.size(); i++) {
			query.setParameter(i, list.get(i));
		}
		return query;
	}

	/**
	 * 分页查询 页数从0开始
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param hql
	 * @param params
	 * @return
	 */
	public List<T> pageQuery(final int pageNo, final int pageSize, final String hql, final Object... params) {

		return (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = null;// createQuery(hql, params);
				query.setFirstResult(pageNo * pageSize);
				query.setMaxResults(pageSize);

				return query.list();
			}
		});
	}

	/**
	 * 根据示例分页查询 页数从0开始
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param example
	 * @return
	 */
	public List<T> pageQueryByExample(final int pageNo, final int pageSize, final T example) {
		List<T> list = getHibernateTemplate().findByExample((T) example, pageNo * pageSize, pageSize);
		return list;
	}

}
