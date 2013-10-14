package com.eastcom.tools.antideadblock.full.balance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-27
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class MiddleLoadBalance extends LoadBalance{
	private Logger logger = LoggerFactory.getLogger(MiddleLoadBalance.class);
	public int middle_balance_interval_minute=3;  //负载处理间隔时间 3
	public HashSet<String> blockedDevices = new HashSet<String>();
	public boolean finish=false;

	public void run(){
		logger.info("start middle load balance.");
		balance();
	}
	abstract boolean isOverload(int num,int limit);
	public void balance(){
		pause(middle_balance_interval_minute*60);
		while(!finish){
			middleBalance();
			if(finish){
				break;
			}
			pause(middle_balance_interval_minute * 60);
		}
		unblockAllDevice();
	}
	public abstract void unblockAllDevice();
	public abstract void middleBalance();
	public void addBlockedDevice(String device){
		this.blockedDevices.add(device);
	}
	public void removeBlockedDevice(String device){
		this.blockedDevices.remove(device);
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}
}
