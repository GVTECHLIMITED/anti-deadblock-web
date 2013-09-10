/**
 * MSISDNCleaner.java was created on 2013年7月28日 下午2:59:47
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.cleaner;

import com.eastcom.tools.antideadblock.ggsn.GGSN;

/**
 * @author sqwen
 */
public interface MSISDNCleaner {

    boolean clean(GGSN ggsn, String msisdn);

}
