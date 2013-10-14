package com.eastcom.tools.antideadblock.full;

import com.eastcom.tools.antideadblock.full.balance.AIEndLoadBalance;
import com.eastcom.tools.antideadblock.full.balance.HWEndLoadBalance;
import com.eastcom.tools.antideadblock.full.balance.HWLoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-22
 * Time: 下午3:08
 * To change this template use File | Settings | File Templates.
 */
public class HwTask extends Task{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private int beginNum;
	private int endNum;
	private int after_handle_max_retry_num=6;   //执行清零命令后查询用户数是否为0的最大次数 6
	private int after_handle_wait_seconds=60;   //执行命令后，查看用户数是否为0的等待时间 60
	private int device_handle_interval_minute=5;  //设备处理 时间间隔 5
	private int after_reset_wait_seconds=10;
	private HwCommand hwCommand;

	public HwTask(){
		super();
		hwCommand = new HwCommand();;
		loadBalance = new HWLoadBalance();

		endLoadBalance = new HWEndLoadBalance();
	}
	@Override
	void execute() {
		try {
			logger.info("start get user num.");
			beginNum = getNum();
			logger.info("get user num end.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("get begin user num error.",e);
			return;
		}
		logger.info("start handle()");
		handle();
		loadBalance.unblockAllDevice();
		logger.info("handle() end.");
		try {
			logger.info("start get user num");
			endNum = getNum();
			logger.info("get user num end.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("get end user num error.",e);
		}
		logger.info("begin user num is "+beginNum+",end user num is "+endNum+",beginNum-endNum="+(beginNum-endNum)+",(beginNum-endNum)/beginNum="+(beginNum-endNum)*1.0/beginNum);
	}
	private int getNum() throws Exception {
		int num=0;
		for(String device:devices){
			try{
				DaoManager.executeLogDao.appendLog(logMap.get(device),"获取用户数");
				hwCommand.login(device);
				int tem = hwCommand.displayUserNumExecute();
				num+=tem;
				hwCommand.disconnect();
			} catch (Exception e){
				DaoManager.executeLogDao.appendLog(logMap.get(device),e.getMessage());
				throw e;
			}
		}
		return num;
	}
	private void handle(){
		for(String device:devices){
			handle(device,5);
		}

		for(String device:devices){
			handle(device,9);
		}

		for(String device:devices){
			handle(device,13);
		}
	}
	private void handle(String device,int slotNum){
		try{
			loadBalance.blockedDevices.remove(device);  //如果已block表中有这个设备，就把这个设备移除掉

			DaoManager.executeLogDao.appendLog(logMap.get(device),"处理slot "+slotNum);
			hwCommand.login(device);
			logger.info("start handle "+device+",slot "+slotNum);
			handle(slotNum);
			pause(device_handle_interval_minute*60);
			loadBalance.middleBalance();
			DaoManager.executeLogDao.appendLog(logMap.get(device),"处理slot"+slotNum+"完成");
		} catch (Exception e) {
			logger.error("",e);
			DaoManager.executeLogDao.appendLog(logMap.get(device),e.getMessage());
		} finally {
			hwCommand.disconnect();
		}
	}
	private void handle(int slotNum) throws Exception {
		hwCommand.systemViewExecute();
		hwCommand.accessViewExecute();
		hwCommand.lockEnableExecute();
		hwCommand.handleHwExecute(slotNum);

		boolean mark=true;
		String result;
		int limit=after_handle_max_retry_num;
		try{
			do{
				pause(after_handle_wait_seconds);
				result = hwCommand.displayNumExecute();
				mark = hwCommand.judgePduNumExecute(slotNum, result.split("\n"));
				logger.info("is zero :"+mark);
			} while( !mark && --limit>0);
			if(!mark){
				throw new Exception("after (deactive pdpcontext slot "+slotNum+"), not 0");
			}
		} catch (Exception e){
			throw e;
		} finally {
			hwCommand.lockUnableExecute();
			hwCommand.quitExecute();
			hwCommand.quitExecute();
		}
	}
}
