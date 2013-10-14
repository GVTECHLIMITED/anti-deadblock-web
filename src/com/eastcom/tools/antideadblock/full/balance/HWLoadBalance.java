package com.eastcom.tools.antideadblock.full.balance;

import com.eastcom.commons.crt.Command;
import com.eastcom.commons.crt.CommandReturn;
import com.eastcom.tools.antideadblock.full.HwCommand;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-11
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class HWLoadBalance extends MiddleLoadBalance{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private LinkedList<String> hwBlockGgsn = new LinkedList<String>();
	private HwCommand hwCommand;
	private int upperLimit = 500000;
	private double capacityProportion = 0.9;
	public HWLoadBalance(){
		this.hwCommand = new HwCommand();
	}

	@Override
	boolean isOverload(int num,int limit) {
		if(num>=limit*capacityProportion){
			logger.info("num>=limit*capacityProportion"+num+">="+limit*capacityProportion);
			return true;
		}
		logger.info("num<limit*capacityProportion"+num+"<"+limit*capacityProportion);
		return false;
	}

	@Override
	public void unblockAllDevice() {
		for(String device:devices){
			try{
				hwCommand.login(device);
				hwCommand.systemViewExecute();
				hwCommand.accessViewExecute();
				hwCommand.lockUnableExecute();
				hwCommand.quitExecute();
				hwCommand.quitExecute();
			} catch (Exception e){
				logger.error("",e);
			}
		}
	}

	@Override
	public void middleBalance() {
		for(String device:devices){
			try{
				hwCommand.login(device);
				middleBalance(device);
				hwCommand.disconnect();
			} catch(Exception e){
				logger.error("",e);
			}
		}
	}
	void middleBalance(String device){
		int num = hwCommand.displayUserNumExecute();
		hwCommand.systemViewExecute();
		hwCommand.accessViewExecute();

		if(isOverload(num,upperLimit)){
			if(blockedDevices.size()>devices.length/2){
				logger.warn("block num is "+blockedDevices.size()+" > devices.length/2 , they are "+blockedDevices);
			} else {
				logger.info("block this device "+device);
				hwCommand.lockEnableExecute();
				addBlockedDevice(device);
			}
		} else {
			if(blockedDevices.contains(device)){
				logger.info("unblock this device "+device);
				hwCommand.lockUnableExecute();
				removeBlockedDevice(device);
			}
		}
		hwCommand.quitExecute();
		hwCommand.quitExecute();
	}
}
