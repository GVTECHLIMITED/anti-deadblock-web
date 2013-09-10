/**
 * MSISDNDetectorImpl.java was created on 2013年7月29日 下午5:06:53
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.detector;

import com.eastcom.commons.crt.Command;
import com.eastcom.commons.crt.CommandReturn;
import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.pool.GGSNSessionPoolFactory;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import com.eastcom.tools.antideadblock.template.ExecTemplateProvider;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sqwen
 */
@Component
public class MSISDNDetectorImpl implements MSISDNDetector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GGSNSessionPoolFactory sessionPoolFactory;

    @Autowired
    private ExecTemplateProvider execTemplateProvider;

    public void setSessionPoolFactory(GGSNSessionPoolFactory sessionPoolFactory) {
        this.sessionPoolFactory = sessionPoolFactory;
    }

    public void setExecTemplateProvider(ExecTemplateProvider execTemplateProvider) {
        this.execTemplateProvider = execTemplateProvider;
    }

    public boolean detected(GGSN ggsn, String msisdn) {
        ExecTemplate template = execTemplateProvider.getExecTemplate(ggsn.getType());
        if (template == null) {
            logger.error("{}: no exec temlate.", ggsn.getType());
            return false;
        }

        ObjectPool<Session> sessionPool = sessionPoolFactory.getMSISDNDetectSessionPool(ggsn.getName());
        Session session = null;
        try {
            session = sessionPool.borrowObject();
            try {
                Command cmd = buildDetectCommand(template.getDetectCommand(msisdn));
                cmd.setEndPromptPattern(template.getCommandPrompt());
                CommandReturn cr = session.execute(cmd);
                return !cr.getReturnedContent().contains(template.getNotExistTag());
            } catch (Exception e) {
                logger.error("Detecting MSISDN " + msisdn + " on " + ggsn.getName() + " failed.", e);
                sessionPool.invalidateObject(session);
                session = null;
            } finally {
                if (session != null) {
                    sessionPool.returnObject(session);
                }
            }
        } catch (Exception be) {
            logger.error("Obtaining GGSN[" + ggsn.getName() + "] logon session failed.", be);
        }

        return false;
    }

    private Command buildDetectCommand(String command) {
        Command cmd = new Command();
        cmd.setCommand(command);
        cmd.setExecutionTimeout(3000);
        return cmd;
    }

}
