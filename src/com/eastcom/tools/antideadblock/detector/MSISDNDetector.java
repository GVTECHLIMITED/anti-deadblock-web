/**
 * MSISDNDetector.java was created on 2013年7月28日 下午7:08:54
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.detector;

import com.eastcom.tools.antideadblock.ggsn.GGSN;

/**
 * @author sqwen
 */
public interface MSISDNDetector {

    boolean detected(GGSN ggsn, String msisdn);

}
