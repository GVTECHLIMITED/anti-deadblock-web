/**
 * AntiDeadBlockApp.java was created on 2013年7月31日 上午1:53:50
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author sqwen
 */
public class AntiDeadBlockApp {

    private static final Logger logger = LoggerFactory.getLogger(AntiDeadBlockApp.class);

    public static void main(String[] args) {
        printVersionInfo();
        try {
            new ClassPathXmlApplicationContext("adb-appctx.xml");
        } catch (BeansException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void printVersionInfo() {
        String version = "EASTCOM IntSight Anti-DeadBlock 1.0.0";
        StringBuilder sb = new StringBuilder();
        String tag = "=";
        for (int i = 0, number = version.length(); i < number; i++) {
            sb.append(tag);
        }
        String tagLine = sb.toString();
        logger.info(tagLine);
        logger.info(version);
        logger.info(tagLine);
    }

}
