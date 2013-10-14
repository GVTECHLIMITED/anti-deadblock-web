/**
 * SessionPoolFactory.java was created on 2013年7月29日 下午9:03:01
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.pool;

import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.ggsn.GGSNProvider;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import com.eastcom.tools.antideadblock.template.ExecTemplateProvider;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sqwen
 */
@Component
public class GGSNSessionPoolFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, ObjectPool<Session>> detectSessionPoolMap;

    private final Map<String, ObjectPool<Session>> cleanSessionPoolMap;

    private final Map<String, ObjectPool<Session>> fullDeactivateSessionPoolMap;

	private ExecTemplateProvider execTemplateProvider;
    @Autowired
    public GGSNSessionPoolFactory(GGSNProvider ggsnProvider,ExecTemplateProvider execTemplateProvider) {
	    this.execTemplateProvider = execTemplateProvider;
        List<GGSN> ggsns = ggsnProvider.findAll();
        detectSessionPoolMap = new HashMap<String, ObjectPool<Session>>(ggsns.size());
        cleanSessionPoolMap = new HashMap<String, ObjectPool<Session>>(ggsns.size());
        fullDeactivateSessionPoolMap = new HashMap<String, ObjectPool<Session>>(ggsns.size());
        for (GGSN ggsn : ggsns) {
	        addGgsn(ggsn);
        }
    }
	public void addGgsn(GGSN ggsn){
		ExecTemplate template = execTemplateProvider.getExecTemplate(ggsn.getType());
		if (template == null) {
			logger.error("Can't init SessionPool for {}: ExecTemplate of type {} does not exist.", ggsn.getName(), ggsn.getType());
			return;
		}

		SessionLogon detectSessionLogon = buildDetectSessionLogon(ggsn, template);
		detectSessionPoolMap.put(ggsn.getName(), new StackObjectPool<Session>(new PoolableSessionFactory(detectSessionLogon)));

		SessionLogon cleanSessionLogon = buildCleanSessionLogon(ggsn, template);
		cleanSessionPoolMap.put(ggsn.getName(), new StackObjectPool<Session>(new PoolableSessionFactory(cleanSessionLogon)));

		SessionLogon fullDeactivateSessionLogon = buildFullDeactivateSessionLogon(ggsn, template);
		fullDeactivateSessionPoolMap.put(ggsn.getName(), new StackObjectPool<Session>(new PoolableSessionFactory(fullDeactivateSessionLogon)));
		logger.info("add ggsn session pool complete.");
	}
	public void deleteGgsn(GGSN ggsn){
		detectSessionPoolMap.remove(ggsn.getName());
		cleanSessionPoolMap.remove(ggsn.getName());
		fullDeactivateSessionPoolMap.remove(ggsn.getName());
		logger.info("delete ggsn session pool complete.");
	}
	public void updateGgsn(GGSN ggsn){
		deleteGgsn(ggsn);
		addGgsn(ggsn);
		logger.info("update ggsn session pool complete.");
	}
    private SessionLogon buildFullDeactivateSessionLogon(GGSN ggsn, ExecTemplate template) {
        SessionLogon sl = buildDetectSessionLogon(ggsn, template);
        sl.setLogfile(ggsn.getLogdir() + "/full/" + ggsn.getName() + ".log");
        return sl;
    }
    private SessionLogon buildDetectSessionLogon(GGSN ggsn, ExecTemplate template) {
        SessionLogon sl = new SessionLogon();
        sl.setProtocol(ggsn.getProtocol());
        sl.setHost(ggsn.getHost());
        sl.setPort(ggsn.getPort());
        sl.setUsernamePrompt(template.getUsernamePrompt());
        sl.setUsername(ggsn.getUsername());
        sl.setPasswordPrompt(template.getPasswordPrompt());
        sl.setPassword(ggsn.getPassword());
        sl.setCommandPrompt(template.getCommandPrompt());
        sl.setLogfile(ggsn.getLogdir() + "/detect/" + ggsn.getName() + ".log");
        return sl;
    }

    private SessionLogon buildCleanSessionLogon(GGSN ggsn, ExecTemplate template) {
        SessionLogon sl = buildDetectSessionLogon(ggsn, template);
        sl.setAfterLogonCommands(template.getEnterCleanModeCommands());
        sl.setLogfile(ggsn.getLogdir() + "/clean/" + ggsn.getName() + ".log");
        return sl;
    }

    public ObjectPool<Session> getMSISDNDetectSessionPool(String ggsnName) {
        return detectSessionPoolMap.get(ggsnName);
    }
    public ObjectPool<Session> getFullDeactivateSessionPool(String ggsnName) {
        return fullDeactivateSessionPoolMap.get(ggsnName);
    }
    public ObjectPool<Session> getMSISDNCleanSessionPool(String ggsnName) {
        return cleanSessionPoolMap.get(ggsnName);
    }

}
