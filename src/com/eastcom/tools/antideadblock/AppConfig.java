/**
 * QueueConfig.java was created on 2013年7月31日 上午3:22:31
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock;

import com.eastcom.tools.antideadblock.task.CleanTask;
import com.eastcom.tools.antideadblock.transport.CleanTaskReceiver;
import com.eastcom.tools.antideadblock.transport.CleanTaskSender;
import com.eastcom.tools.antideadblock.transport.MSISDNReceiver;
import com.eastcom.tools.antideadblock.transport.MSISDNSender;
import com.eastcom.tools.antideadblock.transport.memory.MemoryQueueCleanTaskReceiver;
import com.eastcom.tools.antideadblock.transport.memory.MemoryQueueCleanTaskSender;
import com.eastcom.tools.antideadblock.transport.memory.MemoryQueueMSISDNReceiver;
import com.eastcom.tools.antideadblock.transport.memory.MemoryQueueMSISDNSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author sqwen
 */
@Configuration
public class AppConfig {

    public @Bean
    BlockingQueue<String> msisdnQueue() {
        return new ArrayBlockingQueue<String>(100);
    }

    public @Bean
    BlockingQueue<CleanTask> cleanTaskQueue() {
        return new ArrayBlockingQueue<CleanTask>(100);
    }

    public @Bean
    MSISDNSender msisdnSender() {
        return new MemoryQueueMSISDNSender(msisdnQueue());
    }

    public @Bean
    MSISDNReceiver msisdnReceiver() {
        return new MemoryQueueMSISDNReceiver(msisdnQueue());
    }

    public @Bean
    CleanTaskSender cleanTaskSender() {
        return new MemoryQueueCleanTaskSender(cleanTaskQueue());
    }

    public @Bean
    CleanTaskReceiver cleanTaskReceiver() {
        return new MemoryQueueCleanTaskReceiver(cleanTaskQueue());
    }

}
