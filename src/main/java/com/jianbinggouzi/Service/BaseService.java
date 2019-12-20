package com.jianbinggouzi.Service;

import java.lang.reflect.Field;

import com.jianbinggouzi.Config.EntityClass;
import com.jianbinggouzi.Config.Output;
import com.jianbinggouzi.Dao.BaseDao;
import com.jianbinggouzi.Domain.Dynamics;
import com.jianbinggouzi.Domain.EntityBaseDomain;
import com.jianbinggouzi.Domain.Letter;
import com.jianbinggouzi.Domain.Post;
import com.jianbinggouzi.Domain.TextEntityBaseDomain;

public class BaseService {

	/**
	 * 根据指定的类名分发Dao
	 * 
	 * @param className
	 * @return
	 */
	public <T extends BaseDao> T dispatchDaoByClassName(String className) {
		Field daos[] = getClass().getDeclaredFields();
		for (int i = 0; i < daos.length; i++) {

			if (daos[i].getName().toLowerCase().contains(className.toLowerCase())) {

				try {
					daos[i].setAccessible(true);
					System.out.println("find " + daos[i].getName().toLowerCase());
					return (T) (daos[i].get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					daos[i].setAccessible(false);
				}
			}
		}
		return null;
	}

	/**
	 * 根据参数的类型分发参数对应的Dao
	 * 
	 * @param e
	 *            extends EntityBaseDomain
	 * @return
	 */
	public <T extends BaseDao, E extends EntityBaseDomain> T dispatchDao(E e) {
		System.out.println(e.getClass().getName() + " " + e.getClass().getName().split(".").length);
		String splitName[] = e.getClass().getName().split("\\.");
		String className = splitName[splitName.length - 1];
		Output.log(getClass().getName() + "--------------dispachDao receive:", className);

		Field daos[] = getClass().getDeclaredFields();
		for (int i = 0; i < daos.length; i++) {
			if (daos[i].getName().contains(className.toLowerCase())) {
				Output.log("---------------", "dispatch " + daos[i].getName());
				try {
					daos[i].setAccessible(true);
					return (T) (daos[i].get(this));
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					daos[i].setAccessible(false);
				}
			}
		}
		Output.log("---------------", "dispatch nothing");
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
		else if (textEntityBaseDomain instanceof Post)
			return EntityClass.POST;
		else
			return null;

	}
}
