/**
 * MSISDNCleanTaskWorker.java was created on 2013年7月28日 下午3:03:05
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.cleaner;

import com.eastcom.commons.Processor;
import com.eastcom.tools.antideadblock.task.CleanTask;
import com.eastcom.tools.antideadblock.transport.CleanTaskReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 号码清除任务执行者
 *
 * @author sqwen
 */
public class MSISDNCleanTaskWorker extends Processor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private CleanTaskReceiver cleanTaskReceiver;

    private MSISDNCleaner msisdnCleaner;

    public void setMsisdnCleaner(MSISDNCleaner msisdnCleaner) {
        this.msisdnCleaner = msisdnCleaner;
    }

    public void setCleanTaskReceiver(CleanTaskReceiver cleanTaskReceiver) {
        this.cleanTaskReceiver = cleanTaskReceiver;
    }

    @Override
    protected void doInWorkUnit() throws InterruptedException {
        CleanTask task = cleanTaskReceiver.receive();
        if (task == null) {
            return;
        }

        msisdnCleaner.clean(task.getGGSN(), task.getMSISDN());
    }

}
