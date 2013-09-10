/**
 * GGSNLocatorImpl.java was created on 2013年7月28日 下午9:54:20
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.locator;

import com.eastcom.tools.antideadblock.detector.MSISDNDetector;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.ggsn.GGSNProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author sqwen
 */
@Component("ggsnLocator")
public class GGSNLocatorImpl implements GGSNLocator {

    @Autowired
    private GGSNProvider ggsnProvider;

    @Autowired
    private MSISDNDetector detector;

    public void setGgsnProvider(GGSNProvider ggsnProvider) {
        this.ggsnProvider = ggsnProvider;
    }

    public void setDetector(MSISDNDetector detector) {
        this.detector = detector;
    }

    public GGSN locate(String msisdn) {
        List<GGSN> ggsns = ggsnProvider.findAll();
        for (GGSN ggsn : ggsns) {
            if (detector.detected(ggsn, msisdn)) {
                return ggsn;
            }
        }

        return null;
    }

}
