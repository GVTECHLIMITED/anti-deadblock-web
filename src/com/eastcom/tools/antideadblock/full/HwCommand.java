package com.eastcom.tools.antideadblock.full;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-22
 * Time: 上午10:04
 * To change this template use File | Settings | File Templates.
 */
public class HwCommand extends Command{
	public String systemViewExecute(){
		return execute("system-view");
	}
	public String accessViewExecute(){
		return execute("access-view");
	}
	public String quitExecute(){
		return execute("quit");
	}
	public String lockEnableExecute(){
		return execute("lock slot all enable");
	}
	public String lockUnableExecute(){
		return execute("lock slot all disable");
	}
	public void handleHwExecute(int slotNum){
		execute("deactive pdpcontext slot "+slotNum);
		execute("deactive pdpcontext slot "+(slotNum+2));
	}
	public String displayLockExecute(){
		return execute("display lock all");
	}
	public boolean isAllEnableExecute(){
		String result = displayLockExecute();
		for(String tem : result.split("\n")){
			if(tem.indexOf("使能")!=-1){
				if(tem.equals("不使能")){
					return false;
				}
			}
		}
		return true;
	}
	public boolean isAllDisableExecute(){
		String result = displayLockExecute();
		for(String tem : result.split("\n")){
			if(tem.indexOf("使能")!=-1){
				if(!tem.equals("不使能")){
					return false;
				}
			}
		}
		return true;
	}
	public int displayUserNumExecute(){
		String result = execute("display user-number all");
		return Integer.valueOf(result.substring(result.indexOf("=") + 1).trim());
	}
	public String displayNumExecute(){
		return execute("display pdp-number");
	}
	public boolean isZeroExecute(int slotNum){
		return judgePduNumExecute(slotNum,displayNumExecute().split("\n"));
	}
	public boolean judgePduNumExecute(int slotNum,String str[]){
		int f=0;
		int s=0;
		for (String tem :str){
			if(tem.indexOf("Slot "+slotNum)!=-1){
				f=slotNum;
			}
			if(tem.indexOf("Slot "+(slotNum+1))!=-1){
				f = slotNum+1;
			}

			if(tem.indexOf("Slot "+(slotNum+2))!=-1){
				s = slotNum+2;
			}
			if(tem.indexOf("Slot "+(slotNum+3))!=-1){
				s = slotNum+3;
			}
		}
		logger.info("f="+f+" ,s="+s);
		for (String tem :str){
			if(tem.indexOf("Slot "+f)!=-1){
				int i= Integer.valueOf(tem.substring(tem.indexOf("=") + 1).trim());
				logger.info("i="+i+","+tem);
				if(i!=0){
					return false;
				}
			}
			if(tem.indexOf("Slot "+s)!=-1){
				int i= Integer.valueOf(tem.substring(tem.indexOf("=")+1).trim());
				logger.info("i="+i+","+tem);
				if(i!=0){
					return false;
				}
			}
		}
		return true;
	}
}
