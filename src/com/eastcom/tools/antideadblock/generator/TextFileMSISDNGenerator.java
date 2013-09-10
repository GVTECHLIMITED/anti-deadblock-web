/**
 * TextFileMSISDNGenerator.java was created on 2013年7月28日 下午7:23:03
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.generator;

import com.eastcom.tools.antideadblock.transport.MSISDNSender;
import com.eastcom.tools.antideadblock.util.MSISDNUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URISyntaxException;

/**
 * @author sqwen
 */
@Component("msisdnGenerator")
public class TextFileMSISDNGenerator implements MSISDNGenerator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${phone_file}")
    private String fileName;

    @Autowired
    private MSISDNSender msisdnSender;

    private volatile boolean stop = false;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMsisdnSender(MSISDNSender msisdnSender) {
        this.msisdnSender = msisdnSender;
    }

    public void stop() {
        stop = true;
    }

    @PostConstruct
    @Scheduled(cron = "${msisdn_generator.scheduling}")
    public void generate() {
        logger.info("Start parse MSISDN from {} ...", fileName);
	    File file;
	    InputStream in;
        try {
	        file = new File(getClass().getClassLoader().getResource(fileName).toURI());
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(fileName + " not found.", e);
        } catch (URISyntaxException e) {
	        throw new RuntimeException(fileName + " not found.", e);
        }
	    StopWatch sw = new StopWatch();
        sw.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        int lineNo = 0;
        String shortFileName = file.getName();
        try {
            int count = 0;
            while (!stop && (line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();
                logger.info("Processing line {}: {}", lineNo, line);
                if (line.isEmpty()) {
                    continue;
                }

                int index = line.indexOf('-');
                if (index == -1) {
                    if (!MSISDNUtils.isValid(line)) {
                        logger.error("Invalid MSISDN: {} (Line {} of {})", new Object[] {line, lineNo, shortFileName});
                    } else {
                        msisdnSender.send(line);
                        count++;
                    }
                    continue;
                }

                String startMSISDN = line.substring(0, index);
                if (!MSISDNUtils.isValid(startMSISDN)) {
                    logger.error("Invalid start MSISDN: {} (Line {} of {})", new Object[] {line, lineNo, shortFileName});
                    continue;
                }

                String endMSISDN = line.substring(index + 1);
                if (!MSISDNUtils.isValid(endMSISDN)) {
                    logger.error("Invalid end MSISDN: {} (Line {} of {})", new Object[] {line, lineNo, shortFileName});
                    continue;
                }

                long start =  MSISDNUtils.sanitizeToLong(startMSISDN);
                long end = MSISDNUtils.sanitizeToLong(endMSISDN);
                if (start > end) {
                    end = start + end;
                    start = end - start;
                    end = end - start;
                }
                for (long l = start; l <= end && !stop; l++) {
                    msisdnSender.send(l + "");
                    count++;
                }
            }
            logger.info("{} parsed complted, obtained {} MSISDN. ({} ms)", new Object[] {shortFileName, count, sw.getTime()});
        } catch (IOException e) {
            logger.error("Parse MSISDN from " + fileName + " failed", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

}
