package com.eastcom.tools.antideadblock.full.balance;

import com.eastcom.tools.antideadblock.full.AiCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-11
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
public class AILoadBalance extends MiddleLoadBalance{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Map<String,Integer> aiCapacity = new HashMap<String, Integer>();
	private double capacityProportion = 0.9;
	private AiCommand aiCommand;

	public AILoadBalance(){
		super();
		aiCommand = new AiCommand();
	}
	private void initCapacity(){
		logger.info("start initCapacity.");
		logger.info(Arrays.toString(devices));
		for(String device:devices){
			try{
				aiCommand.login(device);
				int num = aiCommand.getCapacityExecute();
				aiCapacity.put(device,num);
				logger.info("get capacity of "+device+":"+num);
				aiCommand.disconnect();
			} catch (Exception e){
				logger.error("get capacity error:"+device,e);
			}
		}
	}
	@Override
	boolean isOverload(int num,int limit) {
		if(num>=limit*capacityProportion){
			return true;
		}
		return false;
	}

	@Override
	public void unblockAllDevice() {
		for(String device:devices){
			try{
				aiCommand.login(device);
				aiCommand.enterEditExecute();
				aiCommand.unblockedWapExecute();
				aiCommand.unblockedNetExecute();
				aiCommand.commitExecute();
				aiCommand.quitExecute();
			} catch (Exception e){
				logger.error("",e);
			} finally {
				aiCommand.disconnect();
			}
		}
	}

	@Override
	public void middleBalance() {
		initCapacity();
		for(String device:devices){
			try {
				aiCommand.login(device);
				middleBalance(device);
			} catch (Exception e) {
				logger.error("middleBalance error:"+device,e);
			} finally {
				aiCommand.disconnect();
			}
		}
	}
	void middleBalance(String device) throws Exception {
		int num = aiCommand.showNetExecute()+aiCommand.showWapExecute();

		logger.info("device's net + wap = "+num);
		aiCommand.enterEditExecute();
		if(isOverload(num,aiCapacity.get(device))){
			logger.warn(device+" is overload ");
			if(blockedDevices.size()>devices.length/2){
				logger.warn("block num is "+blockedDevices.size()+" > devices.length/2 , they are "+blockedDevices);
			} else {
				aiCommand.blockedNetExecute();
				aiCommand.blockedWapExecute();
				addBlockedDevice(device);
			}
		} else {
			logger.info(device+" is not over load");
			if(blockedDevices.contains(device)){
				logger.info("blockedDevice contains this device:"+device+",then unblocked it.");
				aiCommand.unblockedNetExecute();
				aiCommand.unblockedWapExecute();
				removeBlockedDevice(device);
			}
		}
		aiCommand.commitExecute();
		aiCommand.quitExecute();
	}
}
