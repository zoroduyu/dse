package com.duy.dse.query;

import io.swagger.annotations.ApiModel;

/**
 * Auto-generated: 2018-09-17 21:5:41
 * 索引搜索查询实体类
 * @author duyu
 */
@ApiModel
public class IndexSearchQuery {

	/**
	 * 内容类型
	 */
    private String contentType;
	/**
	 * 搜索内容
	 */
    private String content;
	/**
	 * 返回条数,默认20
	 */
    private Integer rows = 20;
    
    /**
     * 查询的key值字段，数组
     */
    private String[] queryParser;
    
    
	public String[] getQueryParser() {
		return queryParser;
	}
	public void setQueryParser(String[] queryParser) {
		this.queryParser = queryParser;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	@Override
	public String toString() {
		return "IndexSearchQuery [contentType=" + contentType + ", content=" + content + ", rows=" + rows + "]";
	}
     
}