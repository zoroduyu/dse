package com.duy.dse.service;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import com.duy.dse.entity.IndexEntity;
import com.duy.dse.query.IndexQuery;
import com.duy.dse.query.IndexSearchQuery;

/**
 * 索引方法的接口
 * 
 * @author duyu
 *
 */
public interface IndexService {

	/**
	 * 对索引进行添加，更新和删除的方法
	 *  @author duyu
	 *	@param maintainIndexQuery 索引维护接口查询的实体类
	 *	@param syn 是否进行索引同步操作
	 *	@throws IOException io流异常
	 *  2018年10月9日
	 */
	void addOrUpdateIndex(IndexQuery maintainIndexQuery,boolean syn)
			throws IOException;

	/**
	 * 索引搜索接口主方法，用于根据索引查询出匹配的记录
	 * @author duyu
	 * @param indexSearchQuery  查询实体类
	 * @return  查询记录的list
	 * @throws IOException io异常
	 * @throws ParseException 转换异常
	 */
	List<IndexEntity> selectIndexDocmentByConetent(IndexSearchQuery indexSearchQuery) throws IOException, ParseException;

	/**
	 * 批量对索引进行添加，更新和删除的方法
	 *  @author duyu
	 *	@param maintainIndexQuery 索引维护接口查询的实体类
	 *	@throws IOException io流异常
	 *  2018年10月9日
	 */
	void batchmaintainIndex(IndexQuery maintainIndexQuery);
	
}
