package com.eastcom.tools.antideadblock.full;

import com.alibaba.fastjson.JSON;
import com.eastcom.tools.antideadblock.dao.ExecuteLogDao;
import com.eastcom.tools.antideadblock.dao.GGSNTaskDao;
import com.eastcom.tools.antideadblock.dao.data.GGSNTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.ws.Endpoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebService()
@Path("/fulldeactivate")
public class FullDeactivate {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static Map<String,TimerTask> taskMap = new HashMap<String,TimerTask>();
	private static Timer timer = new Timer();
    String hw[] = new String[]{};
    String ai[] = new String[]{};
	public static Map getTaskMap(){
		return taskMap;
	}
    public FullDeactivate(){
	    logger.info("new FullDeactivate instance");
	}
	@GET
	@Produces("text/plain")
	public String getClichedMessage() {
		return "Hello World";
	}
	@POST
	@Produces("application/json")
	@Path("immediateExecute")
	@WebMethod
	public String immediateExecute(String p){
		logger.info("jsonString:"+p);
		try {
			List<String> ggsnNames = JSON.parseObject(p,Parameter.class).getGgsnNames();
			if(ggsnNames==null){
				logger.info("no ggsn");
				return "no ggsn";
			}
			FullDeactivateTask fdt = createTask(ggsnNames,"immediate");
			logger.info("start immediateExecute:"+ggsnNames);

			GGSNTask gt = new GGSNTask();
			gt.setId(fdt.getId());
			gt.setStartTime(new Date());
			gt.setGgsns(ggsnNames.toString());
			gt.setType("立即执行");
			gt.setIsValid("已执行");
			new DaoManager().getGgsnTaskDao().insertTask(gt);

			timer.schedule(fdt,0);
			logger.info("start execute task:"+ggsnNames);
		} catch (Exception e){
			logger.error("",e);
			return e.getMessage();
		}
		return "ok";

	}
	@POST
	@Produces("application/json")
	@Path("timingExecute")
	@WebMethod
    public String timingExecute(String p){
		logger.info("jsonString:"+p);
		try{
			Parameter parameter = JSON.parseObject(p,Parameter.class);
			List<String> ggsnNames = parameter.getGgsnNames();
			if(ggsnNames==null){
				logger.info("no ggsn");
				return "no ggsn";
			}
			String timeStr = parameter.getTime();
			logger.info("start timingExecute :"+timeStr+","+ggsnNames);
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeStr);
				if(date.compareTo(new Date()) < 0){
					return "time before now";
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return "time format was wrong";
			}
			FullDeactivateTask fdt = createTask(ggsnNames,"timing");

			GGSNTask gt = new GGSNTask();
			gt.setId(fdt.getId());
			gt.setStartTime(date);
			gt.setGgsns(ggsnNames.toString());
			gt.setType("定时执行");
			new DaoManager().getGgsnTaskDao().insertTask(gt);
			taskMap.put(gt.getId(),fdt);

			timer.schedule(fdt,date);
			logger.info("add timingExecute task success :"+timeStr+","+ggsnNames);
		} catch (Exception e){
			logger.error("",e);
			return e.getMessage();
		}
		return "ok";
    }
	@POST
	@Produces("application/json")
	@Path("roundExecute")
	@WebMethod
    public String roundExecute(String p){
		logger.info("jsonString:"+p);
		try{
			Parameter parameter = JSON.parseObject(p,Parameter.class);
			List<String> ggsnNames = parameter.getGgsnNames();
			String timeStr = parameter.getTime();
			int interval = parameter.getInterval();
			logger.info("start roundExecute :"+timeStr+","+ggsnNames+","+interval/60);
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeStr);
				if(date.compareTo(new Date()) < 0){
					logger.info("time before now ,set time is now");
					date  = new Date();
				}
			} catch (ParseException e) {
				logger.info("time format is wrong");
				return "time format is wrong";
			}
			FullDeactivateTask fdt = createTask(ggsnNames,"round");

			GGSNTask gt = new GGSNTask();
			gt.setId(fdt.getId());
			gt.setStartTime(date);
			gt.setGgsns(ggsnNames.toString());
			gt.setType("周期执行");
			gt.setInterval(interval);
			new DaoManager().getGgsnTaskDao().insertTask(gt);
			taskMap.put(gt.getId(),fdt);

			timer.scheduleAtFixedRate(fdt, date, interval*1000);
			logger.info("add roundExecute task success :"+timeStr+","+ggsnNames+","+interval/60);
		} catch (Exception e){
			logger.error("",e);
			return e.getMessage();
		}
		return "ok";
    }
	@POST
	@Produces("application/json")
	@Path("deleteTask")
	@WebMethod
	public String deleteTask(String p){
		logger.info("jsonString:"+p);
		try{
			Parameter parameter = JSON.parseObject(p,Parameter.class);
			String id = parameter.getId();
			logger.info("deleteTask :"+id);
			GGSNTask gt = new DaoManager().getGgsnTaskDao().getTaskById(id);
			if(gt ==null){
				logger.info("no that task or has executed:"+p);
				return "no that task or has executed";
			}

			if(gt.getIsValid() !=null && gt.getIsValid().equals("已执行")){
				logger.info("this task is already executed.");
				return "this task is already executed.";
			}
			new DaoManager().getGgsnTaskDao().deleteTaskById(id);
			TimerTask tt = taskMap.remove(id);
			if(tt!=null){
				tt.cancel();
			}
			logger.info(taskMap.toString());
			logger.info("deleteTask success :"+id);
		} catch (Exception e){
			logger.error("",e);
			return e.getMessage();
		}
		return "ok";
	}
	@POST
	@Produces("application/json")
	@Path("updateImmediateTask")
	public String updateImmediateTask(String p){
		logger.info("jsonString:"+p);
		try{
			Parameter parameter = JSON.parseObject(p,Parameter.class);
			String time = parameter.getTime();
			String id=parameter.getId();
			logger.info("updateTask :"+id);
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
				if(date.compareTo(new Date()) < 0){
					logger.info("time before now ,then delete this task");
					return deleteTask("{\"id\":\""+id+"\"}");
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return "time format is wrong";
			}
			GGSNTask gt = new DaoManager().getGgsnTaskDao().getTaskById(id);
			if(gt ==null){
				logger.info("no that task or has executed:"+p);
				return "no that task or has executed";
			}
			if(gt.getIsValid() !=null && gt.getIsValid().equals("已执行")){
				logger.info("this timing task is already executed.");
				return "this timing task is already executed.";
			}
			gt.setStartTime(date);

			FullDeactivateTask tt = (FullDeactivateTask) taskMap.get(id);
			FullDeactivateTask newT = tt.clone();
			taskMap.put(id,newT);
			tt.cancel();

			timer.schedule(newT,date);
			new DaoManager().getGgsnTaskDao().updateTask(gt);
			logger.info("updateTask success :"+id);
		} catch (Exception e){
			logger.error("",e);
			return e.getMessage();
		}
		return "ok";
	}
	@POST
	@Produces("application/json")
	@Path("updateRoundTask")
	public String updateRoundTask(String p){
		logger.info("jsonString:"+p);
		try {
			Parameter parameter = JSON.parseObject(p,Parameter.class);
			String time = parameter.getTime();
			String id = parameter.getId();
			int interval = parameter.getInterval();
			logger.info("updateTask :"+id);
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
				if(date.compareTo(new Date()) < 0){
					logger.info("time before now , then set time is now");
					date  = new Date();
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return "time format is wrong";
			}
			GGSNTask gt = new DaoManager().getGgsnTaskDao().getTaskById(id);
			if(gt == null){
				logger.info("no that task:"+p);
				return "no that task";
			}
			gt.setStartTime(date);
			gt.setInterval(interval);

			FullDeactivateTask tt = (FullDeactivateTask) taskMap.get(id);
			FullDeactivateTask newT = tt.clone();
			taskMap.put(id,newT);
			tt.cancel();

			timer.scheduleAtFixedRate(newT,date,interval*1000);
			new DaoManager().getGgsnTaskDao().updateTask(gt);
			logger.info("updateTask success :"+id);
		} catch (Exception e){
			logger.error("",e);
			return e.getMessage();
		}
		return "ok";
	}
    private FullDeactivateTask createTask(List<String> ggsnNames,String type){
        LinkedList<String> hwList = new LinkedList<String>();
        LinkedList<String> aiList = new LinkedList<String>();
        for(String str:ggsnNames){
            if(str.endsWith("Ber")){
                aiList.add(str);
            } else if(str.endsWith("BHW")){
                hwList.add(str);
            }
        }
        logger.info("hwList "+hwList);
        logger.info("aiList "+aiList);
	    ai = new String[aiList.size()];
	    hw = new String[hwList.size()];
	    for(int i=0;i<ai.length;i++){
		    ai[i] = aiList.get(i);
	    }
	    for(int i=0;i<hw.length;i++){
		    hw[i] = hwList.get(i);
	    }
        FullDeactivateTask fdt = new FullDeactivateTask();
	    fdt.setTaskType(type);
	    fdt.setId(UUID.randomUUID().toString());
        fdt.setAi(ai);
        fdt.setHw(hw);
	    fdt.setExecuteLogDao(new DaoManager().getExecuteLogDao());
        return fdt;
    }
	public static void main(String args[]){
		Object implementor = new FullDeactivate();
		String address = "http://localhost:9000/FullDeactivate";
		Endpoint.publish(address, implementor);
	}
}
