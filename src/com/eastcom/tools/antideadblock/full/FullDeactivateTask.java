package com.eastcom.tools.antideadblock.full;

import com.eastcom.commons.crt.Command;
import com.eastcom.commons.crt.CommandReturn;
import com.eastcom.commons.crt.Session;
import com.eastcom.tools.antideadblock.dao.ExecuteLogDao;
import com.eastcom.tools.antideadblock.dao.data.ExecuteLog;
import com.eastcom.tools.antideadblock.ggsn.GGSN;
import com.eastcom.tools.antideadblock.template.ExecTemplate;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-8-22
 * Time: 下午4:08
 * To change this template use File | Settings | File Templates.
 */
public class FullDeactivateTask extends TimerTask{
	private final Logger logger = LoggerFactory.getLogger(getClass());
	static private GGSNInfoManager ggsnInfoManager;
	private String ggsnName;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private Map<String,Integer> aiCmwapCount = new HashMap<String,Integer>();
    private Map<String,Integer> aiCmnetCount = new HashMap<String,Integer>();
    private Map<String,Integer> hwUserNum = new HashMap<String, Integer>();
    private Map<String,Integer> aiCapacity = new HashMap<String, Integer>();
    private LinkedList<String> hwBlockGgsn = new LinkedList<String>();
    private LinkedList<String> aiBlockGgsn = new LinkedList<String>();

	private String taskType;
	private ExecuteLogDao executeLogDao;
	private Map<String,ExecuteLog> logMap = new HashMap<String, ExecuteLog>();
	public ExecuteLogDao getExecuteLogDao() {
		return executeLogDao;
	}

	public void setExecuteLogDao(ExecuteLogDao executeLogDao) {
		this.executeLogDao = executeLogDao;
	}

	public FullDeactivateTask(){

	}
    public void start(){
	    if(this.taskType.equals("定时执行")){
		    FullDeactivate.getTaskMap().remove(this.getId());
		    new DaoManager().getGgsnTaskDao().deleteTaskById(this.getId());
	    }
        run();
    }
	@Override
	public void run() {
		Calendar date = Calendar.getInstance(Locale.CHINA);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		if(hour!=6){
			String hws ="";
			for(String str:hw){
				hws+=str+",";
			}
			logger.info("hw "+hws);
			String ais ="";
			for(String str:ai){
				ais+=str+",";
			}
			logger.info("ai "+ais);
			return;
		}
        System.out.println("---------------------------------------");
        if(hw.length>=1){
	        boolean markTem = true;
	        for(String str:hw){
				if(!executeLogDao.getExecuteStatusByName(str)){
					markTem = false;
					break;
				}
	        }
	        for(String str:hw){
		        ExecuteLog el = new ExecuteLog();
		        el.setStartTime(new Date());
				el.setId(UUID.randomUUID().toString());
		        el.setGgsnName(str);
		        el.setOperateResult("start");
		        el.setOperateType(taskType);
		        el.setStatus("正在执行");
		        el.setTaskId(id);
		        logMap.put(str,el);
				executeLogDao.insertExecuteLog(el);
	        }
	        if(markTem){
		        executeHW();
	        } else {
		        for(String str:hw){
			        ExecuteLog el = logMap.get(str);
			        executeLogDao.appendLog(el,"当前任务组中有设备正在执行命令,退出.");
			        executeLogDao.update(el);
		        }
	        }
	        for(String str:hw){
		        ExecuteLog el = logMap.get(str);
		        el.setStatus("完成");
		        el.setEndTime(new Date());
		        executeLogDao.update(el);
	        }
        }
        if(ai.length>=1){
	        boolean markTem = true;
	        for(String str:ai){
		        if(!executeLogDao.getExecuteStatusByName(str)){
			        markTem = false;
			        break;
		        }
	        }
	        for(String str:ai){
		        ExecuteLog el = new ExecuteLog();
		        el.setStartTime(new Date());
		        el.setId(UUID.randomUUID().toString());
		        el.setGgsnName(str);
		        el.setOperateResult("start");
		        el.setOperateType(taskType);
		        el.setStatus("正在执行");
		        el.setTaskId(id);
		        logMap.put(str,el);
		        executeLogDao.insertExecuteLog(el);
	        }
	        if(markTem){
		        executeAI();
	        } else {
		        for(String str:ai){
			        ExecuteLog el = logMap.get(str);
			        executeLogDao.appendLog(el,"当前任务组中有设备正在执行命令,退出.");
			        executeLogDao.update(el);
		        }
	        }
	        for(String str:ai){
		        ExecuteLog el = logMap.get(str);
		        el.setStatus("完成");
		        el.setEndTime(new Date());
		        executeLogDao.update(el);
	        }
        }
    }
    String ai[] = new String[]{
            //"WUXGGSN01Ber","WUXGGSN02Ber","WUXGGSN03Ber","WUXGGSN04Ber",
            //"WUXGGSN05Ber","WUXGGSN06Ber","WUXGGSN07Ber","WUXGGSN08Ber",
            //"WUXGGSN09Ber","WUXGGSN10Ber",//
            //"WUXGGSN11Ber"
            //"WUXGGSN12Ber"//,
            //"WUXGGSN13Ber"//
            //"WUXGGSN14Ber","WUXGGSN15Ber","WUXGGSN16Ber"
    };

    String hw[] = new String[]{
            //"WUXGGSN01BHW"
            //"WUXGGSN02BHW",
            //"WUXGGSN03BHW","WUXGGSN04BHW",
            //"WUXGGSN05BHW"
    };

    public void setAi(String[] ai) {
        this.ai = ai;
    }

    public void setHw(String[] hw) {
        this.hw = hw;
    }

    public void executeAI(){
        int intervalOfDevice=10;
        logger.info("start get capacity");
        for(String str:ai){
            execute(str,-1,6);
        }
        logger.info("start count cmwapNum and cmnetNum");
        for (String str:ai){
            execute(str,-1,1);
        }

        HashMap<String,Integer> temCmwap = new HashMap<String,Integer>();
        temCmwap.putAll(aiCmwapCount);

        HashMap<String,Integer> temCmnet = new HashMap<String,Integer>();
        temCmnet.putAll(aiCmnetCount);
        logger.info("wapNum :"+aiCmwapCount);
        logger.info("netNum :"+aiCmnetCount);

        for (String str:ai){
            execute(str,-1,2);
            try {
                logger.info("pause "+intervalOfDevice+"*60*1000");
                Thread.sleep(intervalOfDevice*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0; i< ai.length;i++){
                execute(ai[i],-1,5);
            }
        }
        logger.info("second count");
        try {
            logger.info("pause "+intervalOfDevice+"*60*1000");
            Thread.sleep(intervalOfDevice*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String str:ai){
            execute(str,-1,1);
        }
        logger.info("wapNum :"+aiCmwapCount);
        logger.info("netNum :"+aiCmnetCount);
        int temFInt=0;
        for(int temInt:temCmwap.values()){
            temFInt+=temInt;
        }
        int temSInt=0;
        for (int temInt:aiCmwapCount.values()){
            temSInt+=temInt;
        }
        logger.info("wapNum,secondNum - firstNum = "+(temSInt-temFInt)+" | firstNum = "+temFInt+" | proportion = "+(Math.abs(temSInt-temFInt)/(temFInt*1.0)));

        temFInt=0;
        for(int temInt:temCmnet.values()){
            temFInt+=temInt;
        }
        temSInt=0;
        for (int temInt:aiCmnetCount.values()){
            temSInt+=temInt;
        }
        logger.info("netNum,secondNum - firstNum = "+(temSInt-temFInt)+" | firstNum = "+temFInt+" | proportion = "+(Math.abs(temSInt-temFInt)/(temFInt*1.0)));
    }
    public void executeHW(){
        int intervalOfDevice=5;   //5
        logger.info("start count hwNum");
        for(String str:hw){
            execute(str,-1,3);
        }
        HashMap<String,Integer> temUserNum = new HashMap<String,Integer>();
        temUserNum.putAll(hwUserNum);
        logger.info("hwUserNum are "+hwUserNum);
        for(String str:hw){
            execute(str,5,0);
            try {
                logger.info("pause "+intervalOfDevice+"*60*1000");
                Thread.sleep(intervalOfDevice*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0; i< hw.length;i++){
                execute(hw[i],-1,4);
            }
        }
        for(int i=hw.length-1;i>=0;i--){
            execute(hw[i],9,0);
            try {
                logger.info("pause "+intervalOfDevice+"*60*1000");
                Thread.sleep(intervalOfDevice*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int j = 0; j< hw.length;j++){
                execute(hw[j],-1,4);
            }
        }
        for(String str:hw){
            execute(str,13,0);
            try {
                logger.info("pause "+intervalOfDevice+"*60*1000");
                Thread.sleep(intervalOfDevice*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0; i< hw.length;i++){
                execute(hw[i],-1,4);
            }
        }
        logger.info("second count");
        try {
            logger.info("pause "+intervalOfDevice+"*60*1000");
            Thread.sleep(intervalOfDevice*60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(String str:hw){
            execute(str,-1,3);
        }
        int temFInt=0;
        for(int temInt:temUserNum.values()){
            temFInt+=temInt;
        }
        int temSInt=0;
        for (int temInt:hwUserNum.values()){
            temSInt+=temInt;
        }
        logger.info("hwUseNum,secondNum - firstNum = "+(temSInt-temFInt)+" | firstNum = "+temFInt+" | proportion = "+(Math.abs(temSInt-temFInt)/(temFInt*1.0)));
    }
	public boolean execute(String ggsnName,int slotNum,int mark){ //mark 0:hw  3:countHW 4;hwBalance
	                                                                  //1:countAI 2:ai 5:aiBalance 6 getAiCapacity
        try{
            logger.info("start execute cmd for ggsn : "+ggsnName);
            GGSN ggsn = ggsnInfoManager.getGgsnProvider().findByName(ggsnName);
            if(ggsn == null){
                logger.error("no that ggsn "+ggsnName);
                throw new Exception("no that ggsn "+ggsnName);
            }
            sessionPool =null;
            sessionPool = ggsnInfoManager.getSessionPoolFactory().getFullDeactivateSessionPool(ggsn.getName());
            if(sessionPool == null){
                logger.error("no that sessionPool about "+ggsnName);
                throw new Exception("no that sessionPool about "+ggsnName);
            }
            template = null;
            template = ggsnInfoManager.getExecTemplateProvider().getExecTemplate(ggsn.getType());
            if(template == null){
                logger.error("no that exeTemplate about "+ggsnName);
                throw new Exception("no that exeTemplate about "+ggsnName);
            }
            try {
                session = null;
                //session = sessionPool.borrowObject();
                session = ggsnInfoManager.getGgsnSessionFactory().newSession(ggsnName);
            } catch(Exception e){
                logger.error("Obtaining GGSN[" + ggsnName + "] logon session failed.", e);
                throw new Exception(e);
            }
            this.ggsnName = ggsnName;

            if(ggsnName.endsWith("Ber")){
                if(mark==1){
                    return executeAiCmwapAndCmnet();
                } else if(mark==2){
                    if(executeA("cmwap")) {
                        return executeA("cmnet");
                    }
                    return false;
                } else if(mark == 5){
                    return executeAiBalance();
                } else if(mark == 6){
                    return executeAiGetCapacity();
                }
            } else if(ggsnName.endsWith("BHW")){
                if(mark ==0){
                    return executeH(slotNum);
                } else if(mark==3){
                    return executeHwUserNum();
                } else if(mark == 4){
                    return executeHwBalance();
                }
            }
        } catch (Exception e){
            logger.error("operate failed :"+ggsnName,e);
	        ExecuteLog el = logMap.get(ggsnName);
	        executeLogDao.appendLog(el,e.getMessage());
        } finally {
	        ExecuteLog el = logMap.get(ggsnName);
	        executeLogDao.appendLog(el,ggsnName+","+slotNum+","+mark);
            if(session!=null){
                session.disconnect();
                logger.info("execute session.disconnect()");
            }
        }
        return true;
	}

    Session session = null;

    ObjectPool<Session> sessionPool = null;
    ExecTemplate template =null;
    private boolean executeHwUserNum() throws Exception {
        try{
            StopWatch sw = new StopWatch();
            sw.start();
            Command cmd = buildCommand("display user-number all");
            logger.info("exute cmd :display user-number all");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            CommandReturn cr = session.execute(cmd);
            String result = cr.getReturnedContent();
            hwUserNum.put(ggsnName, Integer.valueOf(result.substring(result.indexOf("=") + 1).trim()));
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("system-view");
            logger.info("execute cmd : system-view");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("access-view");
            logger.info("execute cmd : access-view");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            cmd = buildCommand("lock slot all disable");
            logger.info("exute cmd :lock slot all disable");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("lock slot all disable");
            logger.info("exute cmd :lock slot all disable");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("display lock all");
            logger.info("execute cmd : display lock all");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            for(String tem : result.split("\n")){
                if(tem.indexOf("使能")!=-1 && tem.indexOf("不使能")==-1){
                    logger.warn("不包含\"不使能\""+tem);
                    throw new Exception("display lock all, must be all unable");
                    //
                }
            }

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
        } catch (Exception e){
            throw e;
        } finally {
            logger.info("all cmd is over.");
        }
        return true;
    }
    private boolean executeAiCmwapAndCmnet() throws Exception {
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            Command cmd = buildCommand("show services epg pgw statistics apn cmwap | match Active | match PDP | match contexts | except IP | except DT");
            logger.info("exute cmd :show services epg pgw statistics apn cmwap | match Active | match PDP | match contexts | except IP | except DT");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            CommandReturn cr = session.execute(cmd);
            String result = cr.getReturnedContent();
            aiCmwapCount.put(ggsnName, Integer.valueOf(result.substring(result.indexOf(":") + 1, result.indexOf('\n')).trim()));
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("show services epg pgw statistics apn cmnet | match Active | match PDP | match contexts | except IP | except DT");
            logger.info("exute cmd :show services epg pgw statistics apn cmnet | match Active | match PDP | match contexts | except IP | except DT");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            aiCmnetCount.put(ggsnName, Integer.valueOf(result.substring(result.indexOf(":") + 1, result.indexOf('\n')).trim()));
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());


            cmd = buildCommand("edit");
            logger.info("execute cmd : edit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("set services epg pgw apn cmwap pdp-context creation unblocked ");
            logger.info("execute cmd : set services epg pgw apn cmwap pdp-context creation unblocked ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("set services epg pgw apn cmnet pdp-context creation unblocked ");
            logger.info("execute cmd : set services epg pgw apn cmnet pdp-context creation unblocked ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("commit");
            logger.info("execute cmd : commit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
        } catch(Exception e){
           /*try {
                sessionPool.invalidateObject(session);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            session = null;
            e.printStackTrace();*/
            //logger.error("Exception",e);
            throw e;
            //return false;
        } finally {
            logger.info("all cmd is over.");
            /*if (session != null) {
                try {
                    sessionPool.returnObject(session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
        return true;
    }
    private boolean executeHwBalance() throws Exception {
        logger.info("start executeHwBalance");
        if(hwBlockGgsn.size()>=3){
            logger.warn("block num is "+hwBlockGgsn.size()+" , they are "+hwBlockGgsn);
            return false;
        }
        if(hwBlockGgsn.contains(ggsnName)){
            logger.info("this ggsn is already blocked.");
            return false;
        }
        Command cmd=null;
        CommandReturn cr = null;
        try {
            StopWatch sw = new StopWatch();
            sw.start();

            String result = "";
            cmd = buildCommand("display user-number all");
            logger.info("execute cmd : display user-number all");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            result = cr.getReturnedContent();
            int remInt = Integer.valueOf(result.substring(result.indexOf("=") + 1).trim());


            cmd = buildCommand("system-view");
            logger.info("execute cmd : system-view");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("access-view");
            logger.info("execute cmd : access-view");
            //cmd.setEndPromptPattern("^(\\<\\w+\\>|\\[[\\w\\-]+(access)\\])$");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            if(remInt>500000){
                logger.info(remInt+">500000");
                cmd = buildCommand("lock slot all enable");
                logger.info("execute cmd : lock slot all enable");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                result = cr.getReturnedContent();
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

                cmd = buildCommand("display lock all");
                logger.info("execute cmd : display lock all");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                result = cr.getReturnedContent();
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
                for(String tem : result.split("\n")){
                    if(tem.indexOf("使能")!=-1 && tem.indexOf("不使能")!=-1){
                        logger.warn("包含\"不使能\""+tem);
                        throw new Exception("display lock all, must be all enabled");
                        //
                    }
                }
            } else {
                logger.info(remInt+"<=500000");
                cmd = buildCommand("lock slot all disable");
                logger.info("execute cmd : lock slot all disable");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                result = cr.getReturnedContent();
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

                cmd = buildCommand("display lock all");
                logger.info("execute cmd : display lock all");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                result = cr.getReturnedContent();
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
                for(String tem : result.split("\n")){
                    if(tem.indexOf("使能")!=-1 && tem.indexOf("不使能")==-1){
                        logger.warn("不包含\"不使能\""+tem);
                        throw new Exception("display lock all, must be all unable");
                        //
                    }
                }
            }
            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
        } catch(Exception e){
            throw e;
            //return false;
        } finally {
            logger.info("all cmd is over.");
        }
        return true;
    }
    private boolean executeH(int slotNum) throws Exception {
        Command cmd=null;
        CommandReturn cr = null;
        try {
            StopWatch sw = new StopWatch();
            sw.start();

            String result = "";
            cmd = buildCommand("system-view");
            logger.info("execute cmd : system-view");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("access-view");
            logger.info("execute cmd : access-view");
            //cmd.setEndPromptPattern("^(\\<\\w+\\>|\\[[\\w\\-]+(access)\\])$");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("display lock all");
            logger.info("execute cmd : display lock all");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            for(String tem : result.split("\n")){
                if(tem.indexOf("使能")!=-1 && tem.indexOf("不使能")==-1){
                    logger.warn("包含\"使能\"" +tem);
                    //throw new Exception("display lock all, must be all unabled");
                    //
                }
            }

            cmd = buildCommand("lock slot all enable");
            logger.info("execute cmd : lock slot all enable");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("display lock all");
            logger.info("execute cmd : display lock all");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            for(String tem : result.split("\n")){
                if(tem.indexOf("使能")!=-1 && tem.indexOf("不使能")!=-1){
                    logger.warn("包含\"不使能\""+tem);
                    throw new Exception("display lock all, must be all enabled");
                    //
                }
            }

            cmd = buildCommand("deactive pdpcontext slot "+slotNum);
            logger.info("execute cmd : deactive pdpcontext slot "+slotNum);
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            Thread.sleep(1*60*1000);

            cmd = buildCommand("deactive pdpcontext slot "+(slotNum+2));
            logger.info("execute cmd : deactive pdpcontext slot "+(slotNum+2));
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            Thread.sleep(20*1000);

            cmd = buildCommand("display pdp-number");
            logger.info("execute cmd : display pdp-number");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            int limit=5;
            while(!judgePduNum(slotNum,result.split("\n")) && --limit>0){
                logger.warn("once again.");
                Thread.sleep(60*1000);
                cmd = buildCommand("display pdp-number");
                logger.info("execute cmd : display pdp-number");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                result = cr.getReturnedContent();
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            }
            if(limit==0){
                logger.error("after deactive pdpcontext slot "+slotNum+",return the incorrect result");
                throw new Exception("after deactive pdpcontext slot "+slotNum+",return the incorrect result");
            }

            cmd = buildCommand("lock slot all disable");
            logger.info("execute cmd : lock slot all disable");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("display lock all");
            logger.info("execute cmd : display lock all");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            for(String tem : result.split("\n")){
                if(tem.indexOf("使能")!=-1 && tem.indexOf("不使能")==-1){
                    logger.warn("不包含\"不使能\""+tem);
                    throw new Exception("display lock all, must be all unable");
                    //
                }
            }

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
        } catch(Exception e){
           /*try {
                sessionPool.invalidateObject(session);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            session = null;
            e.printStackTrace();*/
            //logger.error("Exception",e);
            throw e;
            //return false;
        } finally {
            logger.info("all cmd is over.");
            /*if (session != null) {
                try {
                    sessionPool.returnObject(session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
        return true;
    }
    private boolean judgePduNum(int slotNum,String str[]){
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
    private boolean executeAiGetCapacity() throws Exception {
        Command cmd=null;
        CommandReturn cr = null;
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = "";

            cmd = buildCommand("show services epg pgw status | find Node | grep capacity ");
            logger.info("execute cmd : show services epg pgw status | find Node | grep capacity ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            result = cr.getReturnedContent();
            String temStr = result.split("\n")[0];
            int temInt = Integer.valueOf(temStr.substring(temStr.lastIndexOf(":") + 1).trim());
            aiCapacity.put(ggsnName,temInt);
            logger.info("ggsn "+ggsnName+"'s capacity is "+temInt);
        } catch(Exception e){
            throw e;
        } finally {
            logger.info("all cmd is over.");
        }
        return true;
    }

    private boolean executeAiBalance() throws Exception {
        logger.info("start executeAiBalance");
        if(aiBlockGgsn.size()>ai.length/2){
            logger.warn("block num is "+hwBlockGgsn.size()+" , they are "+hwBlockGgsn);
            return false;
        }
        if(aiBlockGgsn.contains(ggsnName)){
            logger.info("this ggsn is already blocked.");
            return false;
        }
        Command cmd=null;
        CommandReturn cr = null;
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = "";

            cmd = buildCommand("show services epg pgw statistics apn cmwap | match Active | match PDP | match contexts | except IP | except DT");
            logger.info("execute cmd : show services epg pgw statistics apn cmwap | match Active | match PDP | match contexts | except IP | except DT ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            int temIntWap = Integer.valueOf(result.substring(result.indexOf(":") + 1).trim());

            cmd = buildCommand("show services epg pgw statistics apn cmnet | match Active | match PDP | match contexts | except IP | except DT");
            logger.info("execute cmd : show services epg pgw statistics apn cmnet | match Active | match PDP | match contexts | except IP | except DT ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            int temIntNet = Integer.valueOf(result.substring(result.indexOf(":") + 1).trim());


            cmd = buildCommand("edit");
            logger.info("execute cmd : edit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            if((temIntNet+temIntWap)>(aiCapacity.get(ggsnName)*0.8)){
                logger.info((temIntNet+temIntWap)+">Capacity*0.8:"+(aiCapacity.get(ggsnName)*0.8));
                cmd = buildCommand("set services epg pgw apn cmwap pdp-context creation blocked ");
                logger.info("execute cmd : set services epg pgw apn cmwap pdp-context creation blocked ");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

                cmd = buildCommand("set services epg pgw apn cmnet pdp-context creation blocked ");
                logger.info("execute cmd : set services epg pgw apn cmnet pdp-context creation blocked ");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            } else {
                logger.info((temIntNet+temIntWap)+"<=Capacity:"+(aiCapacity.get(ggsnName)*0.8));
                cmd = buildCommand("set services epg pgw apn cmwap pdp-context creation unblocked ");
                logger.info("execute cmd : set services epg pgw apn cmwap pdp-context creation unblocked ");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

                cmd = buildCommand("set services epg pgw apn cmnet pdp-context creation unblocked ");
                logger.info("execute cmd : set services epg pgw apn cmnet pdp-context creation unblocked ");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            }
            cmd = buildCommand("commit");
            logger.info("execute cmd : commit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
        } catch(Exception e){
            throw e;
        } finally {
            logger.info("all cmd is over.");
        }
        return true;
    }
    private boolean executeA(String type) throws Exception {
        Command cmd=null;
        CommandReturn cr = null;
        try {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = "";

            cmd = buildCommand("edit");
            logger.info("execute cmd : edit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("set services epg pgw apn "+type+" pdp-context creation blocked ");
            logger.info("execute cmd : set services epg pgw apn "+type+" pdp-context creation blocked ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("commit");
            logger.info("execute cmd : commit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("run request services epg pgw pdp terminate apn "+type+"");
            logger.info("execute cmd : run request services epg pgw pdp terminate apn "+type+"");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("commit");
            logger.info("execute cmd : commit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            Thread.sleep(10*1000);
            cmd = buildCommand("run show services epg pgw statistics apn "+type+" | match Active | match PDP | match contexts | except IP | except DT");
            logger.info("execute cmd : run show services epg pgw statistics apn "+type+" | match Active | match PDP | match contexts | except IP | except DT");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            result = cr.getReturnedContent();
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            int temInt = Integer.valueOf(result.substring(result.indexOf(":")+1,result.indexOf('\n')).trim());
            int limit=5;
            while(temInt!=0 && --limit>0){
                logger.warn("once again");
                Thread.sleep(60*1000);
                cmd = buildCommand("run show services epg pgw statistics apn "+type+" | match Active | match PDP | match contexts | except IP | except DT");
                logger.info("execute cmd : run show services epg pgw statistics apn "+type+" | match Active | match PDP | match contexts | except IP | except DT");
                cmd.setEndPromptPattern(template.getCommandPrompt());
                cr = session.execute(cmd);
                result = cr.getReturnedContent();
                logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
                temInt = Integer.valueOf(result.substring(result.indexOf(":")+1,result.indexOf('\n')).trim());
            }
            if(limit==0){
                logger.error("after set services epg pgw apn "+type+" pdp-context creation blocked ,not 0");
                throw new Exception("after set services epg pgw apn "+type+" pdp-context creation blocked ,not 0");
            }
            cmd = buildCommand("set services epg pgw apn "+type+" pdp-context creation unblocked ");
            logger.info("execute cmd : set services epg pgw apn "+type+" pdp-context creation unblocked ");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());

            cmd = buildCommand("commit");
            logger.info("execute cmd : commit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            logger.info("pause 20*1000");
            Thread.sleep(20*1000);

            cmd = buildCommand("run show services epg pgw statistics apn "+type+" | match Active | match PDP | match contexts | except IP | except DT");
            logger.info("execute cmd : run show services epg pgw statistics apn "+type+" | match Active | match PDP | match contexts | except IP | except DT");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
            result = cr.getReturnedContent();
            temInt = Integer.valueOf(result.substring(result.indexOf(":") + 1, result.indexOf('\n')).trim());
            if(temInt==0){
                logger.error("after set services epg pgw apn "+type+" pdp-context creation unblocked ,mustn't 0");
                throw new Exception("after set services epg pgw apn "+type+" pdp-context creation unblocked ,mustn't 0");
            }

            cmd = buildCommand("quit");
            logger.info("execute cmd : quit");
            cmd.setEndPromptPattern(template.getCommandPrompt());
            cr = session.execute(cmd);
            logger.info("return :"+cr.getReturnedContent()+" "+sw.getTime());
        } catch(Exception e){
           /*try {
                sessionPool.invalidateObject(session);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            session = null;
            e.printStackTrace();*/
            //logger.error("Exception",e);
            throw e;
            //return false;
        } finally {
            logger.info("all cmd is over.");
            /*if (session != null) {
                try {
                    sessionPool.returnObject(session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }
        return true;
    }
	private Command buildCommand(String command) {
		Command cmd = new Command();
		cmd.setCommand(command);
		cmd.setExecutionTimeout(5*1000*60);
		return cmd;
	}

	public Logger getLogger() {
		return logger;
	}

	public String getGgsnName() {
		return ggsnName;
	}

	public void setGgsnName(String ggsnName) {
		this.ggsnName = ggsnName;
	}

	public GGSNInfoManager getGgsnInfoManager() {
		return ggsnInfoManager;
	}

	public void setGgsnInfoManager(GGSNInfoManager ggsnInfoManager) {
		FullDeactivateTask.ggsnInfoManager = ggsnInfoManager;
	}
	public FullDeactivateTask clone(){
		FullDeactivateTask fullDeactivateTask = new FullDeactivateTask();
		fullDeactivateTask.setId(this.getId());
		fullDeactivateTask.setAi(this.ai);
		fullDeactivateTask.setHw(this.hw);
		fullDeactivateTask.setExecuteLogDao(this.executeLogDao);
		fullDeactivateTask.setTaskType(this.taskType);
		return fullDeactivateTask;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	/*public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}*/
}
