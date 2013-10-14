package com.eastcom.tools.antideadblock.ggsn;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-30
 * Time: 下午3:19
 * To change this template use File | Settings | File Templates.
 */
@WebService()
@Path("/ggsnmanager")
public class GGSNManager {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public GGSNManager(){
		logger.info("new GGSNManager instance");
	}
	@GET
	@Produces("text/plain")
	public String getClichedMessage() {
		return "Hello World";
	}

	@POST
	@Produces("application/json")
	@Path("add")
	@WebMethod
	public String add(String str){
		logger.info(str);
		try{
			GGSN ggsn = JSON.parseObject(str, GGSN.class);
			ggsn.setId(UUID.randomUUID().toString());
			new GGSNManagerTask().add(ggsn);
		} catch (Exception e){
			logger.error("",e);
			return "add failed";
		}
		return "add complete";
	}

	@POST
	@Produces("application/json")
	@Path("delete")
	@WebMethod
	public String delete(String str){
		logger.info(str);
		try{
			GGSN ggsn = JSON.parseObject(str, GGSN.class);
			new GGSNManagerTask().delete(ggsn);
		} catch (Exception e){
			logger.error("",e);
			return "delete failed";
		}
		return "delete complete";
	}

	@POST
	@Produces("application/json")
	@Path("update")
	@WebMethod
	public String update(String str){
		logger.info(str);
		try{
			GGSN ggsn = JSON.parseObject(str, GGSN.class);
			new GGSNManagerTask().update(ggsn);
		} catch (Exception e){
			logger.error("",e);
			return "update failed";
		}
		return "update complete";
	}
}
