/**
 * GGSNLocator.java was created on 2013年7月28日 下午3:21:29
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.locator;

import com.eastcom.tools.antideadblock.ggsn.GGSN;

/**
 * @author sqwen
 */
public interface GGSNLocator {

    GGSN locate(String msisdn);

}
