package com.eastcom.tools.antideadblock.full.balance;

import com.eastcom.tools.antideadblock.full.HwCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-27
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class HWEndLoadBalance extends EndLoadBalance{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private HwCommand hwCommand;

	public HWEndLoadBalance(){
		hwCommand = new HwCommand();
	}

	@Override
	void unblockAll() {
		for(String device:devices){
			try {
				hwCommand.login(device);
				hwCommand.systemViewExecute();
				hwCommand.accessViewExecute();
				hwCommand.lockUnableExecute();
				hwCommand.quitExecute();
				hwCommand.quitExecute();
				hwCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void unblockList() {
		for(String device:toUnblockList){
			try {
				hwCommand.login(device);
				hwCommand.systemViewExecute();
				hwCommand.accessViewExecute();
				hwCommand.lockUnableExecute();
				hwCommand.quitExecute();
				hwCommand.quitExecute();
				hwCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void blockList() {
		for(String device:toBlockList){
			try {
				hwCommand.login(device);
				hwCommand.systemViewExecute();
				hwCommand.accessViewExecute();
				hwCommand.lockEnableExecute();
				hwCommand.quitExecute();
				hwCommand.quitExecute();
				hwCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void cleanToAvg() {
		for(String device : toCleanList){
			try {
				hwCommand.login(device);

				hwCommand.systemViewExecute();
				hwCommand.accessViewExecute();

				hwCommand.lockEnableExecute();
				hwCommand.handleHwExecute(5);
				hwCommand.handleHwExecute(9);
				hwCommand.handleHwExecute(13);

				int temInt=0;
				do{
					pause(clearToAvgIntervalSecond);
					temInt = hwCommand.displayUserNumExecute();
				} while (temInt> avg*(1+to_clear_judge_upper_limit_range_num*range));
				hwCommand.lockUnableExecute();
				hwCommand.lockEnableExecute();
				hwCommand.disconnect();
			} catch (Exception e) {
				logger.error("",e);
			}
		}
	}

	@Override
	void getAvgUserNum() {
		int count = 0;
		for(String device:devices){
			try {
				hwCommand.login(device);
			} catch (Exception e) {
				logger.error("",e);
			}
			int num = hwCommand.displayUserNumExecute();
			logger.info("get "+device+" user num:"+num);
			hwCommand.disconnect();
		}
		avg = count/devices.length;
		logger.info("get avg is "+avg);
	}
}
