package com.eastcom.tools.antideadblock.full;

import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-26
 * Time: 下午4:46
 * To change this template use File | Settings | File Templates.
 */
public class CurrentSession {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public Session session = null;
	public ExecTemplate template =null;
	public String ggsnName;
	private static GGSNInfoManager ggsnInfoManager;

	public void login(String currentGgsnName) throws Exception {
		ggsnName = currentGgsnName;
		GGSN ggsn = ggsnInfoManager.getGgsnProvider().findByName(currentGgsnName);
		if(ggsn == null){
			logger.error("no that ggsn "+currentGgsnName);
			throw new Exception("no that ggsn "+currentGgsnName);
		}
		template = ggsnInfoManager.getExecTemplateProvider().getExecTemplate(ggsn.getType());
		if(template == null){
			logger.error("no that exeTemplate about "+currentGgsnName);
			throw new Exception("no that exeTemplate about "+currentGgsnName);
		}
		session = ggsnInfoManager.getGgsnSessionFactory().newSession(ggsn.getName());
		logger.info("start execute cmd at "+currentGgsnName);
	}

	public GGSNInfoManager getGgsnInfoManager() {
		return ggsnInfoManager;
	}

	public void setGgsnInfoManager(GGSNInfoManager ggsnInfoManager) {
		CurrentSession.ggsnInfoManager = ggsnInfoManager;
	}

	public void disconnect(){
		logger.info("disconnect(),session="+ session);
		if(session!=null)
			session.disconnect();
	}
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public ExecTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ExecTemplate template) {
		this.template = template;
	}
}
