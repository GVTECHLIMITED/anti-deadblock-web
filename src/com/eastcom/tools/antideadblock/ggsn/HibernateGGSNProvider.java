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
}
