/**
 * MemoryQueueCleanTaskReceiver.java was created on 2013年7月30日 上午1:20:16
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.transport.memory;

import com.eastcom.tools.antideadblock.task.CleanTask;
import com.eastcom.tools.antideadblock.transport.CleanTaskReceiver;

import java.util.concurrent.BlockingQueue;

/**
 * @author sqwen
 */
//@Component("cleanTaskReceiver")
public class MemoryQueueCleanTaskReceiver implements CleanTaskReceiver {

    private final BlockingQueue<CleanTask> cleanTaskQueue;

    public MemoryQueueCleanTaskReceiver(BlockingQueue<CleanTask> cleanTaskQueue) {
        this.cleanTaskQueue = cleanTaskQueue;
    }

    public CleanTask receive() {
        try {
            return cleanTaskQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

}
