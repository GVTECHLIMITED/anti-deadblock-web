package com.eastcom.tools.antideadblock.full.balance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-27
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
public abstract class EndLoadBalance extends LoadBalance{
	private Logger logger = LoggerFactory.getLogger(EndLoadBalance.class);
	public int end_balance_interval_second=60;  //
	public int clearToAvgIntervalSecond=3;  // 执行清用户命令后 查看当前用户数的间隔时间 3
	public int after_end_sleep_minute=5;    //刷机结束后 停止多长时间 开始 5
	public int to_clear_judge_upper_limit_range_num=2;  //请用户时,判断是否降低到一定的限制，这个限制为平均数乘上 1+ 一个值*range 2
	public boolean needEndLoadBalance=true;
	public LinkedList<String> toCleanList = new LinkedList<String>();
	public LinkedList<String> toBlockList = new LinkedList<String>();
	public LinkedList<String> toUnblockList = new LinkedList<String>();
	public Map<String,Integer> userNumMap = new HashMap<String,Integer>();
	public int avg;
	public double range=0.05;
	public void balance(){
		pause(after_end_sleep_minute*60);
		do {
			logger.info("start getAvgUserNum");
			getAvgUserNum();
			logger.info("start init lists");
			initLists();
			logger.info("start handle lists");
			handleLists();
			if(!needEndLoadBalance){
				break;
			}
			pause(end_balance_interval_second);
		} while(needEndLoadBalance);
		logger.info("start unblockAll");
		unblockAll();
	}
	public void handleLists(){
		if(toCleanList.size() == 0){
			needEndLoadBalance = false;
			logger.info("toCleanList.size() == 0 , end");
			return;
		}
		if(toUnblockList.size() == 0){
			needEndLoadBalance = false;
			logger.info("toUnblockList.size() == 0 , end");
			return;
		}
		logger.info("start unblockList");
		unblockList();
		logger.info("start blockList");
		blockList();
		logger.info("start cleanToAvg");
		cleanToAvg();
	}
	public void initLists(){
		for(String device:devices){
			if(userNumMap.get(device) > avg*(1+range)){
				logger.info("add toCleanList "+device);
				toCleanList.add(device);
			} else if(userNumMap.get(device) < avg*(1-range)){
				logger.info("add toUnblockList "+device);
				toUnblockList.add(device);
			} else {
				logger.info("add toBlockList "+device);
				toBlockList.add(device);
			}
		}
	}
	abstract void unblockAll();
	abstract void unblockList();
	abstract void blockList();
	abstract void cleanToAvg();
	abstract void getAvgUserNum();
}
