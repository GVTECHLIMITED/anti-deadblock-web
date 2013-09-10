package com.eastcom.tools.antideadblock.dao.data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-3
 * Time: 上午9:10
 * To change this template use File | Settings | File Templates.
 */
public class GGSNTask {
	private String id;
	private String ggsns;
	private Date startTime;
	/**
	 * 立即执行
	 * 定时执行
	 * 周期执行
	 */
	private String type;
	private int interval;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGgsns() {
		return ggsns;
	}

	public void setGgsns(String ggsns) {
		this.ggsns = ggsns;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
