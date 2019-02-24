package com.duy.dse.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.duy.dse.entity.IndexLogEntity;
import com.duy.dse.query.IndexLogQuery;

/**
 * @author duyu
 */
public interface IndexLogDao extends BaseDao<IndexLogEntity> {

	/**
	 * 删除待同步的日志记录，只删除status为1的待同步记录
	 * @param msg
	 * @param ip
	 * @return
	 */
	Integer deleteConfirmLog(@Param("msg") String msg,@Param("ip")String ip);
	
	/**
	 * 批量插入日志
	 * @param list
	 */
	Integer bathInsertLog(List<IndexLogEntity> list);
	
	/**
	 * 分页查询
	 * @param indexLogQuery 分页查询类
	 * @return
	 */
	List<IndexLogEntity> selectCommit(IndexLogQuery indexLogQuery);
}