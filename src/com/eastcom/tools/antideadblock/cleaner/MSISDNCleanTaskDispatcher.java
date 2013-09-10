/**
 * MSISDNCleanTaskDispatcher.java was created on 2013年7月28日 下午11:34:06
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.cleaner;

import com.eastcom.commons.Processor;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.locator.GGSNLocator;
import com.eastcom.tools.antideadblock.task.CleanTask;
import com.eastcom.tools.antideadblock.transport.CleanTaskSender;
import com.eastcom.tools.antideadblock.transport.MSISDNReceiver;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 号码清除任务派发器
 *
 * @author sqwen
 */
public class MSISDNCleanTaskDispatcher extends Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Logger msisdnLocateFailedlogger = LoggerFactory.getLogger("locate_failed_msisdn");

    private MSISDNReceiver msisdnReceiver;

    private GGSNLocator ggsnLocator;

    private CleanTaskSender cleanTaskSender;

    public void setMsisdnReceiver(MSISDNReceiver msisdnReceiver) {
        this.msisdnReceiver = msisdnReceiver;
    }

    public void setGgsnLocator(GGSNLocator ggsnLocator) {
        this.ggsnLocator = ggsnLocator;
    }

    public void setCleanTaskSender(CleanTaskSender cleanTaskSender) {
        this.cleanTaskSender = cleanTaskSender;
    }

    @Override
    protected void doInWorkUnit() throws InterruptedException {
        String msisdn = msisdnReceiver.receive();
        logger.info("Obtain MSISDN {}, locating GGSN...", msisdn);
        StopWatch sw = new StopWatch();
        sw.start();
        GGSN ggsn = ggsnLocator.locate(msisdn);
        if (ggsn != null) {
            logger.info("MSISDN {} located to {}. ({} ms)", new Object[] {msisdn, ggsn.getName(), sw.getTime()});
            cleanTaskSender.send(new CleanTask(ggsn, msisdn));
        } else {
            logger.info("MSISDN {} can't locate to any GGSN. ({} ms)", new Object[] {msisdn, sw.getTime()});
            msisdnLocateFailedlogger.info(msisdn);
        }
    }

}
