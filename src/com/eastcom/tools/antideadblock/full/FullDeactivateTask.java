package com.eastcom.tools.antideadblock.full;

import com.eastcom.commons.crt.Command;
import com.eastcom.commons.crt.CommandReturn;
import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.dao.ExecuteLogDao;
import com.eastcom.tools.antideadblock.dao.data.ExecuteLog;
import com.eastcom.tools.antideadblock.full.balance.HWLoadBalance;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-8-22
 * Time: 下午4:08
 * To change this template use File | Settings | File Templates.
 */
public class FullDeactivateTask extends TimerTask{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String id;
	private String taskType;
	private ExecuteLogDao executeLogDao;
	String ai[];
	String hw[];
	public FullDeactivateTask(){
	}
    public void start(){
        run();
    }
	@Override
	public void run() {

		if(ai.length>0){
			Task task = new AiTask();
			task.setExecuteLogDao(executeLogDao);
			task.setTaskType(taskType);
			task.setTask_id(id);
			task.setDevices(ai);
			task.start();
		}
		if(hw.length>0){
			Task task = new HwTask();
			task.setExecuteLogDao(executeLogDao);
			task.setTaskType(taskType);
			task.setTask_id(id);
			task.setDevices(hw);
			task.start();
		}
    }
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
    public void setAi(String[] ai) {
        this.ai = ai;
    }

    public void setHw(String[] hw) {
        this.hw = hw;
    }

	public Logger getLogger() {
		return logger;
	}

	public FullDeactivateTask clone(){
		FullDeactivateTask fullDeactivateTask = new FullDeactivateTask();
		fullDeactivateTask.setId(this.getId());
		fullDeactivateTask.setAi(this.ai);
		fullDeactivateTask.setHw(this.hw);
		fullDeactivateTask.setExecuteLogDao(this.executeLogDao);
		fullDeactivateTask.setTaskType(this.taskType);
		return fullDeactivateTask;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public ExecuteLogDao getExecuteLogDao() {
		return executeLogDao;
	}

	public void setExecuteLogDao(ExecuteLogDao executeLogDao) {
		this.executeLogDao = executeLogDao;
	}
}
