package com.eastcom.tools.antideadblock.full.balance;

import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.full.GGSNInfoManager;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-9
 * Time: 下午5:47
 * To change this template use File | Settings | File Templates.
 */
public class LoadBalance extends Thread{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String device[];
	private Session session = null;
	private GGSNInfoManager ggsnInfoManager;
	private String ggsnName;

	public void run(){
		for(String ggsn:device){
			this.ggsnName = ggsn;
			balanceExecute();
		}
	}
	private void balanceExecute(){
		try{
			initSession();
		} catch (Exception e){

		}

	}
	private void initSession() throws Exception {
		logger.info("start execute cmd for ggsn : "+ggsnName);
		GGSN ggsn = ggsnInfoManager.getGgsnProvider().findByName(ggsnName);
		if(ggsn == null){
			logger.error("no that ggsn "+ggsnName);
			throw new Exception("no that ggsn "+ggsnName);
		}
		ObjectPool<Session> sessionPool = null;
		ExecTemplate template =null;
		sessionPool = ggsnInfoManager.getSessionPoolFactory().getFullDeactivateSessionPool(ggsn.getName());
		if(sessionPool == null){
			logger.error("no that sessionPool about "+ggsnName);
			throw new Exception("no that sessionPool about "+ggsnName);
		}
		template = ggsnInfoManager.getExecTemplateProvider().getExecTemplate(ggsn.getType());
		if(template == null){
			logger.error("no that exeTemplate about "+ggsnName);
			throw new Exception("no that exeTemplate about "+ggsnName);
		}
		try {
			session = null;
			session = ggsnInfoManager.getGgsnSessionFactory().newSession(ggsnName);
		} catch(Exception e){
			logger.error("Obtaining GGSN[" + ggsnName + "] logon session failed.", e);
			throw new Exception(e);
		}
	}
}
