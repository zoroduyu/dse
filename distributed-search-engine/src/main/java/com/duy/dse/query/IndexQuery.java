package com.duy.dse.query;

import java.util.List;

import com.duy.dse.entity.IndexEntity;

/**
 * 索引维护接口查询的实体类
 * @author duyu
 *
 */
public class IndexQuery {

	/**
	 * 内容类型
	 */
	private String contentType;
	
	/**
	 * 操作类型
	 */
	private String malongalongype;
	
	/**
	 * 索引内容
	 */
	private List<IndexEntity> indexs;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	

	public String getMalongalongype() {
		return malongalongype;
	}

	public void setMalongalongype(String malongalongype) {
		this.malongalongype = malongalongype;
	}

	public List<IndexEntity> getIndexs() {
		return indexs;
	}

	public void setIndexs(List<IndexEntity> indexs) {
		this.indexs = indexs;
	}
	
}
