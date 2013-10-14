package com.eastcom.tools.antideadblock.full.balance;

import com.eastcom.commons.crt.Command;
import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.full.GGSNInfoManager;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-9
 * Time: 下午5:47
 * To change this template use File | Settings | File Templates.
 */
public abstract class LoadBalance extends Thread{
	private Logger logger = LoggerFactory.getLogger(getClass());
	public String devices[];
	public void run(){
		balance();
	}
	abstract void balance();
	protected void pause(int num){
		logger.info("pause "+num+" seconds");
		try {
			Thread.sleep(num*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setDevices(String[] devices) {
		this.devices = devices;
	}
}
