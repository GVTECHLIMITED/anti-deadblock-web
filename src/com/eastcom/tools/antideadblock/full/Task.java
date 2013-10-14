package com.eastcom.tools.antideadblock.full;

import com.eastcom.tools.antideadblock.dao.ExecuteLogDao;
import com.eastcom.tools.antideadblock.dao.data.ExecuteLog;
import com.eastcom.tools.antideadblock.full.balance.EndLoadBalance;
import com.eastcom.tools.antideadblock.full.balance.MiddleLoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-22
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
abstract class Task extends Thread{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public String[] devices;

	public Map<String,ExecuteLog> logMap = new HashMap<String, ExecuteLog>();
	public String task_id;
	public ExecuteLogDao executeLogDao;
	public String taskType;
	public MiddleLoadBalance loadBalance;
	public EndLoadBalance endLoadBalance;

	public void start(){
		logger.info(Arrays.toString(devices));
		logger.info(task_id);
		logger.info(taskType);
		logger.info(executeLogDao.toString());

		super.start();
	}
	public void run() {
		setExecuted();

		Calendar date = Calendar.getInstance(Locale.CHINA);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		if(hour >= 6){
			return;
		}
		addLog();
		if(judge()){
			for(String str_:devices){
				ExecuteLog el = logMap.get(str_);
				el.setStatus("正在执行");
				executeLogDao.update(el);
			}
			execute();
			/*endLoadBalance.start();
			try {
				endLoadBalance.join();
			} catch (InterruptedException e) {
				logger.error("",e);
			}*/
		} else {
			for(String str_:devices){
				ExecuteLog el = logMap.get(str_);
				executeLogDao.appendLog(el,"当前任务组中有设备正在执行命令,退出.");
			}
			logger.warn("当前任务组中有设备正在执行命令,退出.");
		}
		loadBalance.setFinish(true);
		setCompleteLog();
	}

	private void setCompleteLog(){
		for(String str:devices){
			ExecuteLog el = logMap.get(str);
			el.setStatus("完成");
			el.setEndTime(new Date());
			executeLogDao.update(el);
		}
	}
	private boolean judge(){
		for(String str:devices){
			if(!executeLogDao.getExecuteStatusByName(str)){
				return false;
			}
		}
		return true;
	}
	private void addLog(){
		for(String str:devices){
			ExecuteLog el = new ExecuteLog();
			el.setStartTime(new Date());
			el.setId(UUID.randomUUID().toString());
			el.setGgsnName(str);
			el.setOperateResult("start");
			el.setOperateType(taskType);
			el.setStatus("待验证");
			el.setTaskId(task_id);
			logMap.put(str,el);
			executeLogDao.insertExecuteLog(el);
		}
	}
	protected void setExecuted(){
		if(this.taskType.equals("timing") ){
			FullDeactivate.getTaskMap().remove(this.getId());
			DaoManager.ggsnTaskDao.markExecuted(this.getTask_id());
		}
		if(this.taskType.equals("immediate")){
			DaoManager.ggsnTaskDao.markExecuted(this.getTask_id());
		}
	}
	protected void pause(int num){
		logger.info("pause "+num+" seconds");
		try {
			Thread.sleep(num*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	abstract void execute();

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String id) {
		this.task_id = id;
	}


	public void setDevices(String[] devices) {
		this.devices = devices;
		loadBalance.setDevices(devices);
		endLoadBalance.setDevices(devices);
	}

	void setExecuteLogDao(ExecuteLogDao executeLogDao) {
		this.executeLogDao = executeLogDao;
	}

	void setTaskType(String taskType) {
		this.taskType = taskType;
	}
}
