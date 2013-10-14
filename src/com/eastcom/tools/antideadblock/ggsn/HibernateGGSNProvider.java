package com.eastcom.tools.antideadblock.ggsn;

import com.eastcom.tools.antideadblock.dao.GGSNDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-3
 * Time: 上午10:03
 * To change this template use File | Settings | File Templates.
 */
@Component
public class HibernateGGSNProvider implements GGSNProvider {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<GGSN> ggsns;

	private Map<String, GGSN> map;

	@Autowired
	public HibernateGGSNProvider(GGSNDao ggsnDao) {
		ggsns = ggsnDao.findAll();
		if(ggsns==null){
			logger.error("no ggsns");
			return;
		}
		map = new HashMap<String, GGSN>(ggsns.size());
		for (GGSN ggsn : ggsns) {
			map.put(ggsn.getName(), ggsn);
		}
		logger.info(map.toString());
	}
	@Override
	public List<GGSN> findAll() {
		if(ggsns==null){
			return new LinkedList<GGSN>();
		}
		return ggsns;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public GGSN findByName(String name) {
		return map.get(name);  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public GGSN findByGtpCAddress(String address) {
		for(GGSN ggsn:map.values()){
			if(ggsn.getGtpCAddress()!=null && ggsn.getGtpCAddress().equals(address)){
				return ggsn;
			}
		}
		return null;
	}

	@Override
	public GGSN findById(String id) {
		for(GGSN ggsn:map.values()){
			if(ggsn.getId()!=null && ggsn.getId().equals(id)){
				return ggsn;
			}
		}
		return null;
	}
	public void add(GGSN ggsn){
		ggsns.add(ggsn);
		map.put(ggsn.getName(),ggsn);
		logger.info("add ggsn complete.ggsn.size() = "+ggsns);
	}
	public void delete(GGSN ggsn){
		ggsns.remove(findById(ggsn.getId()));
		map.remove(ggsn.getName());
		logger.info("delete ggsn complete..size() = "+ggsns.size());
	}
	public void update(GGSN ggsn){
		delete(ggsn);
		add(ggsn);
	}
}
