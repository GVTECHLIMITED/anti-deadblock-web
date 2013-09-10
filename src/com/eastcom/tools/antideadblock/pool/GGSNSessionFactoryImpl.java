/**
 * GGSNSessionFactoryImpl.java was created on 2013年8月28日 下午2:39:44
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
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.ggsn.GGSNProvider;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import com.eastcom.tools.antideadblock.template.ExecTemplateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author sqwen
 */
@Component
public class GGSNSessionFactoryImpl implements GGSNSessionFactory {

    @Autowired
    private GGSNProvider ggsnProvider;

    @Autowired
    private ExecTemplateProvider execTemplateProvider;

    public Session newSession(String ggsnName) throws Exception {
        GGSN ggsn = ggsnProvider.findByName(ggsnName);
        if (ggsn == null) {
            throw new IllegalArgumentException("No GGSN: " + ggsnName);
        }

        ExecTemplate execTempalte = execTemplateProvider.getExecTemplate(ggsn.getType());
        if (execTempalte == null) {
            throw new IllegalStateException("NO ExecTemplate defined for: " + ggsn.getType());
        }

        SessionLogon sessionLogon = buildFullDeactivateSessionLogon(ggsn, execTempalte);
        return makeSession(sessionLogon);
    }

    private SessionLogon buildFullDeactivateSessionLogon(GGSN ggsn, ExecTemplate template) {
        SessionLogon sl = new SessionLogon();
        sl.setProtocol(ggsn.getProtocol());
        sl.setHost(ggsn.getHost());
        sl.setPort(ggsn.getPort());
        sl.setUsernamePrompt(template.getUsernamePrompt());
        sl.setUsername(ggsn.getUsername());
        sl.setPasswordPrompt(template.getPasswordPrompt());
        sl.setPassword(ggsn.getPassword());
        sl.setCommandPrompt(template.getCommandPrompt());
        sl.setLogfile(ggsn.getLogdir() + "/full/" + ggsn.getName() + ".log");
        return sl;
    }

    private Session makeSession(SessionLogon sessionLogon) throws Exception {
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

}
