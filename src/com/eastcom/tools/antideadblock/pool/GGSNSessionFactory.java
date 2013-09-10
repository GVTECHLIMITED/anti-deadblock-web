/**
 * SessionFactory.java was created on 2013年8月28日 下午2:19:14
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.pool;

import com.eastcom.commons.crt.Session;

/**
 * @author sqwen
 */
public interface GGSNSessionFactory {

    Session newSession(String ggsnName) throws Exception;

}
