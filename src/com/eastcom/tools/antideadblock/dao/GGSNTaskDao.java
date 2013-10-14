package com.eastcom.tools.antideadblock.dao;

import com.eastcom.tools.antideadblock.dao.data.GGSNTask;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-3
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
public class GGSNTaskDao extends HibernateDaoSupport {

	public void insertTask(GGSNTask gt){
		getHibernateTemplate().save(gt);
	}
	public void empty(){
		getHibernateTemplate().deleteAll(findAll());
	}
	public List<GGSNTask> findAll(){
		return (List<GGSNTask>) getHibernateTemplate().find("from GGSNTask");
	}
	public void markExecuted(String id){
		GGSNTask gt = (GGSNTask) getHibernateTemplate().get(GGSNTask.class,id);
		gt.setIsValid("已执行");
		updateTask(gt);
	}
	public void deleteTaskById(String id){
		GGSNTask gt = (GGSNTask) getHibernateTemplate().get(GGSNTask.class,id);
		if(gt==null){
			logger.info("no that task:"+id);
			return;
		}
		getHibernateTemplate().delete(gt);
	}
	public GGSNTask getTaskById(String id){
		return (GGSNTask) getHibernateTemplate().get(GGSNTask.class,id);
	}
	public void updateTask(GGSNTask gt){
		getHibernateTemplate().update(gt);
	}
}
