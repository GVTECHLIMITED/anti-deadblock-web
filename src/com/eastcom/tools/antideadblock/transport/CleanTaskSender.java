/**
 * CleanTaskSender.java was created on 2013年7月29日 上午12:55:17
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.transport;

import com.eastcom.tools.antideadblock.task.CleanTask;

/**
 * @author sqwen
 */
public interface CleanTaskSender {

    void send(CleanTask task);

}
