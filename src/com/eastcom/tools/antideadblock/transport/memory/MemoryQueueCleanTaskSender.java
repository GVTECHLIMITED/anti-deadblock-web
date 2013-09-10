/**
 * MemoryQueueCleanTaskSender.java was created on 2013年7月30日 上午1:18:04
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.transport.memory;

import com.eastcom.tools.antideadblock.task.CleanTask;
import com.eastcom.tools.antideadblock.transport.CleanTaskSender;

import java.util.concurrent.BlockingQueue;

/**
 * @author sqwen
 */
//@Component("cleanTaskSender")
public class MemoryQueueCleanTaskSender implements CleanTaskSender {

//    @Autowired
//    @Qualifier("cleanTaskQueue")
    private final BlockingQueue<CleanTask> cleanTaskQueue;

    public MemoryQueueCleanTaskSender(BlockingQueue<CleanTask> cleanTaskQueue) {
        this.cleanTaskQueue = cleanTaskQueue;
    }

    public void send(CleanTask cleanTask) {
        try {
            cleanTaskQueue.put(cleanTask);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
