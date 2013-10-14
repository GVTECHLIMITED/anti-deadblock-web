package com.eastcom.tools.antideadblock.full;

import com.eastcom.tools.antideadblock.full.balance.AIEndLoadBalance;
import com.eastcom.tools.antideadblock.full.balance.AILoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-22
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class AiTask extends Task{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String,Integer> capacityMap = new HashMap<String, Integer>();
	private int beginNum[]; // 0:wap 1:net
	private int endNum[];
	private int after_handle_max_retry_num=6;  //清零后查看是否为0 最大次数 6
	private int after_handle_wait_seconds=60;  //清零后 查看是否为0 等待时间 60
	private int device_handle_interval_minute=10; //设备间 间隔时间 10分钟
	private int after_reset_wait_seconds=10; //unblock后，查看是否不为0等待时间 10
	private AiCommand aiCommand;

	public AiTask(){
		super();
		aiCommand= new AiCommand();
		loadBalance = new AILoadBalance();

		endLoadBalance = new AIEndLoadBalance();
	}
	public void execute(){
		try {
			logger.info("start init capacity map");
			initCapacityMap();
			logger.info("init capacity end.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("init capacity error",e);
			return;
		}
		if(1==1){
			return;
		}
		try {
			logger.info("start get wap and net num.");
			beginNum = getNum();
			logger.info("get wap and net num end.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("get wap and net num error",e);
			return;
		}
		logger.info("start handle()");
		handle();
		loadBalance.unblockAllDevice();
		logger.info("handle() end.");
		try {
			logger.info("start get wap and net num.");
			endNum = getNum();
			logger.info("get wap and net num end.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("get wap and net num error",e);
		}
		logger.info("begin wap num is "+beginNum[0]+",end wap num is "+endNum[0]+",begin-end ="+(beginNum[0]-endNum[0])+",(begin-end)/begin ="+(beginNum[0]-endNum[0])*1.0/beginNum[0]);
		logger.info("begin net num is "+beginNum[1]+",end net num is "+endNum[1]+",begin-end ="+(beginNum[1]-endNum[1])+",(begin-end)/begin ="+(beginNum[1]-endNum[1])*1.0/beginNum[1]);
		logger.info("begin count is "+(beginNum[0]+beginNum[1])+",end count is "+(endNum[0]+endNum[1])+",(begin-end)/begin ="+(beginNum[0]+beginNum[1]-endNum[0]-endNum[1])*1.0/(beginNum[0]+beginNum[1]));
	}
	private void initCapacityMap() throws Exception {
		for(String device:devices){
			try{
				DaoManager.executeLogDao.appendLog(logMap.get(device),"获取capacity");
				aiCommand.login(device);
				int capacity = aiCommand.getCapacityExecute();
				logger.info("get "+device+" capacity num:"+capacity);
				capacityMap.put(device,capacity);
				aiCommand.disconnect();
			} catch (Exception e){
				DaoManager.executeLogDao.appendLog(logMap.get(device),e.getMessage());
				throw e;
			}
		}
	}
	private int[] getNum() throws Exception {
		int num[] = new int[2];
		for(String device:devices){
			try{
				DaoManager.executeLogDao.appendLog(logMap.get(device),"获取用户数");
				logger.info("start get "+device+" wap and net num.");
				aiCommand.login(device);
				int wap = aiCommand.showWapExecute();
				logger.info("get wap num:"+wap);
				int net = aiCommand.showNetExecute();
				logger.info("get net num:"+net);
				num[0] += wap;
				num[1] += net;
				aiCommand.disconnect();
			} catch (Exception e){
				DaoManager.executeLogDao.appendLog(logMap.get(device),e.getMessage());
				throw e;
			}
		}
		logger.info("all device wap num:"+num[0]+",net num:"+num[1]);
		return num;
	}
	private void handle(){
		for(String device:devices){
			loadBalance.blockedDevices.remove(device); //如果已blocked表中有 这个设备，就移除这个设备
			try{
				aiCommand.login(device);
				DaoManager.executeLogDao.appendLog(logMap.get(device),"开始刷wap");
				handleWap();
				DaoManager.executeLogDao.appendLog(logMap.get(device),"开始刷net");
				handleNet();
				DaoManager.executeLogDao.appendLog(logMap.get(device),"刷机完成");
			} catch (Exception e){
				e.printStackTrace();
				DaoManager.executeLogDao.appendLog(logMap.get(device),e.getMessage());
			} finally {
				aiCommand.disconnect();
			}
			pause(device_handle_interval_minute*60);
			loadBalance.middleBalance();
		}
	}
	private void handleWap() throws Exception {
		aiCommand.enterEditExecute();
		aiCommand.blockedWapExecute();
		aiCommand.commitExecute();
		try{
			aiCommand.handleWapExecute();
			aiCommand.commitExecute();
			aiCommand.quitExecute();
			int limit=after_handle_max_retry_num;
			int temInt;
			do{
				pause(after_handle_wait_seconds);
				temInt = aiCommand.showWapExecute();
				logger.info("get wap num:"+temInt);
			}	while(temInt!=0 && --limit>0);
			if(temInt!=0){
				throw new Exception("after (run request services epg pgw pdp terminate apn cmwap), not 0");
			}
		} catch (Exception e){
			throw e;
		} finally {
			aiCommand.enterEditExecute();
			aiCommand.unblockedWapExecute();
			aiCommand.commitExecute();
			aiCommand.quitExecute();
		}
		if(!isNormalAfterClear("cmwap")){
			throw new Exception("after (unblock cmwap), always 0");
		}
	}
	private void handleNet() throws Exception {
		aiCommand.enterEditExecute();
		aiCommand.blockedNetExecute();
		aiCommand.commitExecute();
		try{
			aiCommand.	handleNetExecute();
			aiCommand.commitExecute();
			aiCommand.quitExecute();
			int limit=after_handle_max_retry_num;
			int temInt;
			do{
				pause(after_handle_wait_seconds);
				temInt = aiCommand.showNetExecute();
				logger.info("get net num:"+temInt);
			}	while(temInt!=0 && --limit>0);
			if(temInt!=0){
				throw new Exception("after (run request services epg pgw pdp terminate apn cmnet), not 0");
			}
		} catch (Exception e){
			throw e;
		} finally {
			aiCommand.enterEditExecute();
			aiCommand.unblockedNetExecute();
			aiCommand.commitExecute();
			aiCommand.quitExecute();
		}
		if(!isNormalAfterClear("cmnet")){
			throw new Exception("after (unblock cmnet), always 0");
		}
	}
	public boolean isNormalAfterClear(String type){
		pause(after_reset_wait_seconds);
		if(type.equals("cmwap")){
			if(aiCommand.showWapExecute()>0){
				return true;
			} else {
				return false;
			}
		} else if(type.equals("cmnet")){
			if(aiCommand.showNetExecute()>0){
				return true;
			} else {
				return false;
			}
		}

		return false;
	}
}
