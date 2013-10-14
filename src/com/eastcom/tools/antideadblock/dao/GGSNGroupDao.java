package com.eastcom.tools.antideadblock.dao;

import com.eastcom.tools.antideadblock.dao.data.GGSNGroup;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-29
 * Time: 下午3:53
 * To change this template use File | Settings | File Templates.
 */
public class GGSNGroupDao extends HibernateDaoSupport {
	public GGSNGroup findById(String id){
		return getHibernateTemplate().get(GGSNGroup.class,id);
	}
	public void contrast(String id){
		GGSNGroup ggsnGroup = findById(id);
		if(ggsnGroup==null){
			ggsnGroup = new GGSNGroup();
			ggsnGroup.setId(id);
			ggsnGroup.setName(id);
			save(ggsnGroup);
		}
	}
	public void save(GGSNGroup ggsnGroup){
		getHibernateTemplate().save(ggsnGroup);
	}
}
