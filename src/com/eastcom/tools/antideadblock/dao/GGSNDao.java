package com.eastcom.tools.antideadblock.dao;

import com.eastcom.tools.antideadblock.ggsn.GGSN;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-3
 * Time: 上午9:08
 * To change this template use File | Settings | File Templates.
 */
public class GGSNDao extends HibernateDaoSupport {
	public GGSNDao(){
		System.out.println("---------------------------------------GGSNDao-----");
	}
	public List<GGSN> findAll(){
		return (List<GGSN>)getHibernateTemplate().find("from GGSN");
	}
	public void update(GGSN ggsn){
		ggsn.setUpdateTime(new Date());
		getHibernateTemplate().update(ggsn);
	}
	public void delete(String id){
		getHibernateTemplate().delete(getHibernateTemplate().get(GGSN.class,id));
	}
	public void save(GGSN ggsn){
		getHibernateTemplate().save(ggsn);
	}
}
