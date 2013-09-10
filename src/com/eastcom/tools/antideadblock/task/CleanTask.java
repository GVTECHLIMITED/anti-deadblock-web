/**
 * CleanTask.java was created on 2013年7月28日 下午9:55:52
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.task;

import com.eastcom.tools.antideadblock.ggsn.GGSN;

/**
 * @author sqwen
 */
public class CleanTask {

    private final GGSN GGSN;

    private final String MSISDN;

    public CleanTask(GGSN GGSN, String MSISDN) {
        this.GGSN = GGSN;
        this.MSISDN = MSISDN;
    }

    public GGSN getGGSN() {
        return GGSN;
    }

    public String getMSISDN() {
        return MSISDN;
    }

}
