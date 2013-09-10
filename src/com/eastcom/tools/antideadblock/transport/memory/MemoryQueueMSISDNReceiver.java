/**
 * MemoryQueueMSISDNReceiver.java was created on 2013年7月28日 下午6:55:27
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.transport.memory;

import com.eastcom.tools.antideadblock.transport.MSISDNReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.BlockingQueue;

/**
 * @author sqwen
 */
//@Component("msisdnReceiver")
public class MemoryQueueMSISDNReceiver implements MSISDNReceiver {

    private final BlockingQueue<String> msisdnQueue;

    @Autowired
    public MemoryQueueMSISDNReceiver(@Qualifier("msisdnQueue") BlockingQueue<String> msisdnQueue) {
        this.msisdnQueue = msisdnQueue;
    }

    public String receive() {
        try {
            return msisdnQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

}
