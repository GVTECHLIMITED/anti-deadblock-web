package com.eastcom.tools.antideadblock.dao.data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Rembau
 * Date: 13-9-3
 * Time: 上午9:33
 * To change this template use File | Settings | File Templates.
 */
public class ExecuteLog {
	private String id;
	private String ggsnName;
	/**
	 * 立即执行
	 * 定时执行
	 * 周期执行
	 */
	private String operateType;
	private Date startTime;
	private Date endTime;
	/**
	 * 正在执行
	 * 执行完成
	 */
	private String status;
	/**
	 * 	ggsn正在操作，本次操作取消,
	 * 	append形式把所有日志记录下来
	 */
	private String operateResult;
	private String taskId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGgsnName() {
		return ggsnName;
	}

	public void setGgsnName(String ggsnName) {
		this.ggsnName = ggsnName;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperateResult() {
		return operateResult;
	}

	public void setOperateResult(String operateResult) {
		this.operateResult = operateResult;
	}
}
