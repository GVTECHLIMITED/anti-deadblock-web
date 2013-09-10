/**
 * SessionLogon.java was created on 2013年7月30日 下午10:10:37
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.pool;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author sqwen
 */
public class SessionLogon {

    private String protocol = "telnet";

    private String host;

    private int port;

    private String usernamePrompt;

    private String username;

    private String passwordPrompt;

    private String password;

    private String commandPrompt;

    private String[] afterLogonCommands;

    private String logfile;

    public String getLogfile() {
        if (logfile == null) {
            return null;
        }

        // TODO 支持变量替换
        return logfile;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsernamePrompt() {
        return usernamePrompt;
    }

    public void setUsernamePrompt(String usernamePrompt) {
        this.usernamePrompt = usernamePrompt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordPrompt() {
        return passwordPrompt;
    }

    public void setPasswordPrompt(String passwordPrompt) {
        this.passwordPrompt = passwordPrompt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommandPrompt() {
        return commandPrompt;
    }

    public void setCommandPrompt(String commandPrompt) {
        this.commandPrompt = commandPrompt;
    }

    public String[] getAfterLogonCommands() {
        return afterLogonCommands;
    }

    public void setAfterLogonCommands(String[] afterLogonCommands) {
        this.afterLogonCommands = afterLogonCommands;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setLogfile(String logfile) {
        this.logfile = logfile;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

}
