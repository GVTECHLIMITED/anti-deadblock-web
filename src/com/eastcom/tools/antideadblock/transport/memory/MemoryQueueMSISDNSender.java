/**
 * MemoryQueueMSISDNSender.java was created on 2013年7月28日 下午6:40:18
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.transport.memory;

import com.eastcom.tools.antideadblock.transport.MSISDNSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.BlockingQueue;

import static com.eastcom.tools.antideadblock.util.MSISDNUtils.sanitizeToString;

/**
 * @author sqwen
 */
//@Component("msisdnSender")
public class MemoryQueueMSISDNSender implements MSISDNSender {

    private final BlockingQueue<String> msisdnQueue;

    @Autowired
    public MemoryQueueMSISDNSender(@Qualifier("msisdnQueue") BlockingQueue<String> msisdnQueue) {
        this.msisdnQueue = msisdnQueue;
    }

    public void send(String msisdn) {
        try {
            msisdnQueue.put(sanitizeToString(msisdn));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
