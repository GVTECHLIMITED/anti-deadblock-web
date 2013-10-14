package com.eastcom.tools.antideadblock.ggsn;

import com.eastcom.tools.antideadblock.full.DaoManager;
import com.eastcom.tools.antideadblock.full.GGSNInfoManager;

/**
 * Created with IntelliJ IDEA.
 * User: rembau
 * Date: 13-9-30
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
public class GGSNManagerTask {
	private static GGSNInfoManager ggsnInfoManager;

	public void add(GGSN ggsn){
		ggsnInfoManager.getGgsnProvider().add(ggsn);
		ggsnInfoManager.getSessionPoolFactory().addGgsn(ggsn);
		new DaoManager().getGgsnDao().save(ggsn);
	}

	public void delete(GGSN ggsn){
		ggsnInfoManager.getGgsnProvider().delete(ggsn);
		ggsnInfoManager.getSessionPoolFactory().deleteGgsn(ggsn);
		new DaoManager().getGgsnDao().delete(ggsn.getId());
	}

	public void update(GGSN ggsn){
		ggsnInfoManager.getGgsnProvider().update(ggsn);
		ggsnInfoManager.getSessionPoolFactory().updateGgsn(ggsn);
		new DaoManager().getGgsnDao().update(ggsn);
	}

	public GGSNInfoManager getGgsnInfoManager() {
		return ggsnInfoManager;
	}

	public void setGgsnInfoManager(GGSNInfoManager ggsnInfoManager) {
		GGSNManagerTask.ggsnInfoManager = ggsnInfoManager;
	}
}
