package com.eastcom.tools.antideadblock.full;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-4
 * Time: 下午1:05
 * To change this template use File | Settings | File Templates.
 */
public class Parameter {
	private String id;
	private String time;
	private List<String> ggsnNames;
	private int interval;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<String> getGgsnNames() {
		return ggsnNames;
	}

	public void setGgsnNames(List<String> ggsnNames) {
		this.ggsnNames = ggsnNames;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
