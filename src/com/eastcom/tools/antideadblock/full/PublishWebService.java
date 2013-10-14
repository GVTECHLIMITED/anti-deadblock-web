package com.eastcom.tools.antideadblock.full;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.ws.Endpoint;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-8-23
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
//@Component("publishWebService")
public class PublishWebService {
	//@Autowired
	FullDeactivate fullDeactivate;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private int port;
	@Autowired
	public PublishWebService(@Value("${fullDeactivatePort}") int port){
		this.port = port;
	}
	@PostConstruct
	private void publish(){
		logger.info("start publish");
		try{
			String address = "http://192.168.1.21:"+port+"/FullDeactivate";
			Endpoint.publish(address, fullDeactivate);
		} catch (Exception e){
			e.printStackTrace();
		}
		logger.info("publish success!");
	}
}
