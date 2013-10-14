package com.eastcom.tools.antideadblock.full;

import com.eastcom.commons.crt.CommandReturn;
import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-22
 * Time: 上午10:07
 * To change this template use File | Settings | File Templates.
 */
public class Command extends CurrentSession{
	public static Logger logger = LoggerFactory.getLogger(Command.class);
	public String execute(String str){
		com.eastcom.commons.crt.Command cmd = buildCommand(str);
		logger.info("execute:"+str);
		cmd.setEndPromptPattern(template.getCommandPrompt());
		CommandReturn cr = session.execute(cmd);
		logger.info("result"+cr.getReturnedContent());
		return cr.getReturnedContent();
	}
	private com.eastcom.commons.crt.Command buildCommand(String command) {
		com.eastcom.commons.crt.Command cmd = new com.eastcom.commons.crt.Command();
		cmd.setCommand(command);
		cmd.setExecutionTimeout(5*1000*60);
		return cmd;
	}
}
