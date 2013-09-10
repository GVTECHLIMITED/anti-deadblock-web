package com.eastcom.tools.antideadblock.dao;

import com.eastcom.tools.antideadblock.dao.data.ExecuteLog;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-3
 * Time: 上午9:42
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteLogDao extends HibernateDaoSupport {

	public void appendLog(ExecuteLog el,String log){
		el.setOperateResult(el.getOperateResult()+"--"+log);
		getHibernateTemplate().update(el);
	}
	public void update(ExecuteLog el){
		getHibernateTemplate().update(el);
	}
	public void saveOrUpdate(ExecuteLog el){
		getHibernateTemplate().saveOrUpdate(el);
	}

	/**
	 * 初始化时删除所有正在执行的日志记录
	 */
	public void init(){
		for(ExecuteLog el:(List<ExecuteLog>)getHibernateTemplate().find("from ExecuteLog where status = ?","正在执行")){
			el.setStatus("上次程序执行中该设备没有执行完成");
			getHibernateTemplate().update(el);
		}
	}

	public void insertExecuteLog(ExecuteLog el){
		getHibernateTemplate().save(el);
	}
	public boolean getExecuteStatusByName(String name){
		String sql = "select status from ExecuteLog where ggsnName=?";
		List<String> list = (List<String>) getHibernateTemplate().find(sql,name);
		for(String str:list){
			if(str.equals("正在执行")){
				return false;
			}
		}
		return true;
	}
}
