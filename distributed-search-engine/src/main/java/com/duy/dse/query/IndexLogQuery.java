package com.duy.dse.query;

public class IndexLogQuery {

	private String ip;
	
	private Integer status;
	
	private Integer pageNo = 0;
	
	private Integer pageSize = 5;

	
	
	public IndexLogQuery() {
		super();
	}

	public IndexLogQuery(String ip, Integer status, Integer pageNo, Integer pageSize) {
		super();
		this.ip = ip;
		this.status = status;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "IndexLogQuery [ip=" + ip + ", status=" + status + ", pageNo=" + pageNo + ", pageSize=" + pageSize + "]";
	}
	
}
