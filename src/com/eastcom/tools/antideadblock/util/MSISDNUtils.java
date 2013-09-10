/**
 * MSISDNUtils.java was created on 2013年7月28日 下午3:04:56
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.util;

/**
 * @author sqwen
 */
public class MSISDNUtils {

    private MSISDNUtils() {}

    /**
     * 将号码进行标准化
     * @param origMSISDN 原始号码
     * @return 标准化后的号码
     */
    public static final String sanitizeToString(String origMSISDN) {
        origMSISDN = origMSISDN.trim();
        return origMSISDN.startsWith("86") ? origMSISDN : "86" + origMSISDN;
    }

    /**
     * 将号码转换为长整形
     * @param origMSISDN 原始号码
     * @return 标准化为长整形的号码
     */
    public static final long sanitizeToLong(String origMSISDN) {
        return Long.parseLong(sanitizeToString(origMSISDN));
    }

    public static final boolean isValid(String msisdn) {
        return msisdn.length() == 11 || msisdn.length() == 13 && msisdn.startsWith("86");
    }

}
