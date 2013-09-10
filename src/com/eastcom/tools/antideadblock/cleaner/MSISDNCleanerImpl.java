/**
 * MSISDNCleanerImpl.java was created on 2013年7月29日 下午9:47:36
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.cleaner;

import com.eastcom.commons.crt.Command;
import com.eastcom.commons.crt.CommandReturn;
import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.pool.GGSNSessionPoolFactory;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import com.eastcom.tools.antideadblock.template.ExecTemplateProvider;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sqwen
 */
@Component("msisdnCleaner")
public class MSISDNCleanerImpl implements MSISDNCleaner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Logger msisdnCleanFailedlogger = LoggerFactory.getLogger("clean_failed_msisdn");

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

    public boolean clean(GGSN ggsn, String msisdn) {
        ExecTemplate template = execTemplateProvider.getExecTemplate(ggsn.getType());
        if (template == null) {
            logger.error("{}: no exec temlate.", ggsn.getType());
            return false;
        }

        StopWatch sw = new StopWatch();
        sw.start();
        logger.info("Deactive/terminate {} context on {}...", msisdn, ggsn.getName());

        ObjectPool<Session> sessionPool = sessionPoolFactory.getMSISDNCleanSessionPool(ggsn.getName());
        Session session = null;
        try {
            session = sessionPool.borrowObject();
            try {
                Command cmd = buildCommand(template.getCleanCommand(msisdn));
                cmd.setEndPromptPattern(template.getCommandPrompt());
                CommandReturn cr = session.execute(cmd);
                boolean result = !cr.getReturnedContent().contains(template.getNotExistTag());
                logger.info("Deactive/terminate {} context on {} {}. ({} ms)", new Object[] {msisdn, ggsn.getName(), result ? "SUCCESS" : "FAILED", sw.getTime()});
                if (!result) {
                    msisdnCleanFailedlogger.info("{}@{}: no such msisdn", msisdn, ggsn.getName());
                }
                return result;
            } catch (Exception e) {
                logger.error("Deactive/terminate " + msisdn + " context on " + ggsn.getName() + " failed.", e);
                msisdnCleanFailedlogger.info("{}@{}: deactive/terminate exception", msisdn, ggsn.getName());
                sessionPool.invalidateObject(session);
                session = null;
            } finally {
                if (session != null) {
                    sessionPool.returnObject(session);
                }
            }
        } catch (Exception be) {
            logger.error("Obtaining GGSN[" + ggsn.getName() + "] logon session failed.", be);
            msisdnCleanFailedlogger.info("{}@{}: login failed", msisdn, ggsn.getName());
        }

        return false;
    }

    private Command buildCommand(String command) {
        Command cmd = new Command();
        cmd.setCommand(command);
        cmd.setExecutionTimeout(3000);
        return cmd;
    }

}
