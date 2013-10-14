package com.eastcom.tools.antideadblock.full.balance;

import com.eastcom.tools.antideadblock.full.AiCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-27
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public class AIEndLoadBalance extends EndLoadBalance{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private AiCommand aiCommand;

	public AIEndLoadBalance(){
		aiCommand = new AiCommand();
	}

	@Override
	void unblockAll() {
		for(String device:devices){
			try {
				aiCommand.login(device);
				aiCommand.enterEditExecute();
				aiCommand.unblockedNetExecute();
				aiCommand.commitExecute();
				aiCommand.unblockedWapExecute();
				aiCommand.commitExecute();
				aiCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void unblockList() {
		for(String device:toUnblockList){
			try {
				aiCommand.login(device);
				aiCommand.enterEditExecute();
				aiCommand.unblockedNetExecute();
				aiCommand.commitExecute();
				aiCommand.unblockedWapExecute();
				aiCommand.commitExecute();
				aiCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void blockList() {
		for(String device:toBlockList){
			try {
				aiCommand.login(device);
				aiCommand.enterEditExecute();
				aiCommand.blockedWapExecute();
				aiCommand.commitExecute();
				aiCommand.blockedNetExecute();
				aiCommand.commitExecute();
				aiCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void cleanToAvg() {
		for(String device : toCleanList){
			try {
				aiCommand.login(device);
				aiCommand.enterEditExecute();

				aiCommand.blockedNetExecute();
				aiCommand.commitExecute();
				aiCommand.blockedWapExecute();
				aiCommand.commitExecute();

				aiCommand.handleNetExecute();
				aiCommand.commitExecute();
				aiCommand.handleWapExecute();
				aiCommand.commitExecute();
				aiCommand.quitExecute();

				int temInt=0;
				do{
					pause(clearToAvgIntervalSecond);
					temInt = aiCommand.showNetExecute();
					temInt += aiCommand.showWapExecute();
					logger.info("user num is "+temInt+",avg*(1+range) is "+avg*(1+range));
				} while (temInt> avg*(1+to_clear_judge_upper_limit_range_num*range));
				aiCommand.enterEditExecute();

				aiCommand.unblockedWapExecute();
				aiCommand.commitExecute();
				aiCommand.unblockedNetExecute();
				aiCommand.commitExecute();

				aiCommand.blockedWapExecute();
				aiCommand.commitExecute();
				aiCommand.blockedNetExecute();
				aiCommand.commitExecute();
				aiCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	public void getAvgUserNum(){
		int count = 0;
		for(String device:devices){
			try {
				aiCommand.login(device);
			} catch (Exception e) {
				logger.error("",e);
			}
			int wapNum = aiCommand.showWapExecute();
			int netNum = aiCommand.showNetExecute();
			logger.info("get "+device+" user num,wap="+wapNum+",net="+netNum+",wap+net="+(wapNum+netNum));
			userNumMap.put(device,wapNum+netNum);
			count+=wapNum;
			count+=netNum;
			aiCommand.disconnect();
		}
		avg = count/devices.length;
		logger.info("get avg is "+avg);
	}
}
