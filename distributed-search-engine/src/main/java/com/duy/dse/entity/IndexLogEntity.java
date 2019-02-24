package com.duy.dse.entity;

import java.sql.Date;

/**
 * 索引进行日志同步的日志实体类
 * @author duyu
 *
 */
public class IndexLogEntity {

	/**
	 * ip地址
	 */
	private String ip;
	
	/**
	 * 存储的该操作的json报文
	 */
	private String msg;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 0:  已插入的日志   1:待同步的日志
	 */
	private int status;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "IndexLogEntity [ip=" + ip + ", msg=" + msg + ", createTime=" + createTime + ", status=" + status + "]";
	}
	
}
