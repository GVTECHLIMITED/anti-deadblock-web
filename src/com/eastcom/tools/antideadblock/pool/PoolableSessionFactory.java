/**
 * PoolableSessionFactory.java was created on 2013年7月30日 下午9:14:38
 *
 * Copyright (c) 2013 EASTCOM Software Technology Co., Ltd. All rights reserved.
 */
package com.eastcom.tools.antideadblock.pool;

import com.eastcom.commons.crt.*;
import com.eastcom.commons.crt.ssh2.Ssh2LogonSetting;
import com.eastcom.commons.crt.ssh2.Ssh2Protocol;
import com.eastcom.commons.crt.ssh2.Ssh2Session;
import com.eastcom.commons.crt.telnet.TelnetLogonSetting;
import com.eastcom.commons.crt.telnet.TelnetProtocol;
import com.eastcom.commons.crt.telnet.TelnetSession;
import org.apache.commons.pool.BasePoolableObjectFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author sqwen
 */
public class PoolableSessionFactory extends BasePoolableObjectFactory<Session> {

    private final SessionLogon sessionLogon;

    public PoolableSessionFactory(SessionLogon sessionLogon) {
        this.sessionLogon = sessionLogon;
    }

    @Override
    public Session makeObject() throws Exception {
        Session session = null;
        if ("telnet".equalsIgnoreCase(sessionLogon.getProtocol())) {
            session = buildTelnetSession(sessionLogon);
        } else if ("ssh".equalsIgnoreCase(sessionLogon.getProtocol())) {
            session = buildSSHSession(sessionLogon);
        }

        Command cmd = new Command();
        cmd.setEndPromptPattern(sessionLogon.getCommandPrompt());
        if (sessionLogon.getAfterLogonCommands() != null) {
            for (String command : sessionLogon.getAfterLogonCommands()) {
                cmd.setCommand(command);
                session.execute(cmd);
            }
        }

        return session;
    }

    private Session buildTelnetSession(SessionLogon sessionLogon) {
        Session session = new TelnetSession();
        initSessionLogging(session, sessionLogon);

        TelnetProtocol protocol = new TelnetProtocol();
        protocol.setHost(sessionLogon.getHost());
        protocol.setPort(sessionLogon.getPort());
        session.connect(protocol);

        TelnetLogonSetting setting = new TelnetLogonSetting();
        AutoAction username = new AutoAction();
        username.setExpectPromptPattern(sessionLogon.getUsernamePrompt());
        username.setReponseCommand(sessionLogon.getUsername());
        AutoAction password = new AutoAction();
        password.setExpectPromptPattern(sessionLogon.getPasswordPrompt());
        password.setReponseCommand(sessionLogon.getPassword());
        setting.addAutoLogonAction(username);
        setting.addAutoLogonAction(password);
        setting.setLogonSuccessPromptPattern(sessionLogon.getCommandPrompt());

        ShellContext sc = new ShellContext();
        sc.setCommandPromptPattern(sessionLogon.getCommandPrompt());
        sc.setCommandPromptRegex(true);
        sc.setPaginationPromptPattern("^(---\\(more\\)---|\\s+---- More ----)$");
        sc.setPaginationPromptRegex(true);
        session.setShellContext(sc);

        session.logon(setting);
        return session;
    }

    private Session buildSSHSession(SessionLogon sessionLogon) {
        Session session = new Ssh2Session();
        initSessionLogging(session, sessionLogon);

        Ssh2Protocol protocol = new Ssh2Protocol();
        protocol.setHost(sessionLogon.getHost());
        protocol.setPort(sessionLogon.getPort());
        protocol.setLoginName(sessionLogon.getUsername());
        session.connect(protocol);

        Ssh2LogonSetting setting = new Ssh2LogonSetting();
        setting.setPassword(sessionLogon.getPassword());
        setting.setLogonSuccessPromptPattern(sessionLogon.getCommandPrompt());
        setting.setLogonSuccessPromptRegex(true);

        ShellContext sc = new ShellContext();
        sc.setCommandPromptPattern(sessionLogon.getCommandPrompt());
        sc.setCommandPromptRegex(true);
        sc.setPaginationPromptPattern("^(---\\(more\\)---|\\s+---- More ----)$");
        sc.setPaginationPromptRegex(true);
        session.setShellContext(sc);

        session.logon(setting);
        return session;
    }

    private void initSessionLogging(Session session, SessionLogon sessionLogon) {
        if (sessionLogon.getLogfile() == null) {
            return;
        }

        File file = new File(sessionLogon.getLogfile());
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return;
            }
        }

        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new RuntimeException("Can't logging session to file: " + sessionLogon.getLogfile(), e);
        }
        session.addListener(new PrintWriterSessionResponseRecorder(writer));
    }

    @Override
    public void destroyObject(Session session) throws Exception {
        session.disconnect();
    }

}
