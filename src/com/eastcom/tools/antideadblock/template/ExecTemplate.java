/**
 * ExecTemplate.java was created on 2013年7月29日 下午5:31:35
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.template;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 执行模版
 *
 * @author sqwen
 */
public class ExecTemplate {

    private String type;

    private String usernamePrompt;

    private String passwordPrompt;

    private String commandPrompt;

    private String detectCommandTemplate;

    private String[] enterCleanModeCommands;

    private String cleanCommandTemplate;

    private String notExistTag;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getDetectCommand(String msisdn) {
        return String.format(detectCommandTemplate, msisdn);
    }

    public String getCleanCommand(String msisdn) {
        return String.format(cleanCommandTemplate, msisdn);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetectCommandTemplate() {
        return detectCommandTemplate;
    }

    public void setDetectCommandTemplate(String detectCommandTemplate) {
        this.detectCommandTemplate = detectCommandTemplate;
    }

    public String getNotExistTag() {
        return notExistTag;
    }

    public void setNotExistTag(String notExistTag) {
        this.notExistTag = notExistTag;
    }

    public String getCleanCommandTemplate() {
        return cleanCommandTemplate;
    }

    public void setCleanCommandTemplate(String cleanCommandTemplate) {
        this.cleanCommandTemplate = cleanCommandTemplate;
    }

    public String getUsernamePrompt() {
        return usernamePrompt;
    }

    public void setUsernamePrompt(String usernamePrompt) {
        this.usernamePrompt = usernamePrompt;
    }

    public String getPasswordPrompt() {
        return passwordPrompt;
    }

    public void setPasswordPrompt(String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }

    public String getCommandPrompt() {
        return commandPrompt;
    }

    public void setCommandPrompt(String commandPrompt) {
        this.commandPrompt = commandPrompt;
    }

    public String[] getEnterCleanModeCommands() {
        return enterCleanModeCommands;
    }

    public void setEnterCleanModeCommands(String[] enterCleanModeCommands) {
        this.enterCleanModeCommands = enterCleanModeCommands;
    }

}
