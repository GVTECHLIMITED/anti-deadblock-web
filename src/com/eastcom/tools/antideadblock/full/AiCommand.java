package com.eastcom.tools.antideadblock.full;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-22
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
public class AiCommand extends Command{

	public int showWapExecute(){
		String temStr = execute("show services epg pgw statistics apn cmwap | match Active | match PDP | match contexts | except IP | except DT");
		return Integer.valueOf(temStr.substring(temStr.indexOf(":") + 1, temStr.indexOf('\n')).trim());
	}
	public int showNetExecute(){
		String temStr = execute("show services epg pgw statistics apn cmnet | match Active | match PDP | match contexts | except IP | except DT");
		return Integer.valueOf(temStr.substring(temStr.indexOf(":") + 1, temStr.indexOf('\n')).trim());
	}
	public String enterEditExecute(){
		return execute("edit");
	}
	public String commitExecute(){
		return execute("commit");
	}
	public String quitExecute(){
		return execute("quit");
	}
	public int getCapacityExecute(){
		String result = execute("show services epg pgw status | find Node | grep capacity");
		String temStr = result.split("\n")[0];
		int temInt = Integer.valueOf(temStr.substring(temStr.lastIndexOf(":") + 1).trim());
		return temInt;
	}
	public String blockedWapExecute(){
		return execute("set services epg pgw apn cmwap pdp-context creation blocked");
	}
	public String blockedNetExecute(){
		return execute("set services epg pgw apn cmnet pdp-context creation blocked");
	}
	public String unblockedWapExecute(){
		return execute("set services epg pgw apn cmwap pdp-context creation unblocked ");
	}
	public String unblockedNetExecute(){
		return execute("set services epg pgw apn cmnet pdp-context creation unblocked ");
	}
	public String handleWapExecute(){
		return execute("run request services epg pgw pdp terminate apn cmwap");
	}
	public String handleNetExecute(){
		return execute("run request services epg pgw pdp terminate apn cmnet");
	}
}
