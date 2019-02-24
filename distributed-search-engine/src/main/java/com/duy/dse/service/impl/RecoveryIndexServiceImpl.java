package com.duy.dse.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.duy.dse.dao.IndexLogDao;
import com.duy.dse.entity.IndexLogEntity;
import com.duy.dse.exception.BusinessException;
import com.duy.dse.query.IndexLogQuery;
import com.duy.dse.query.IndexQuery;
import com.duy.dse.service.IndexService;
import com.duy.dse.service.RecoveryIndexService;
import com.duy.dse.util.IpAddressUtil;

@Service
public class RecoveryIndexServiceImpl  implements RecoveryIndexService{

	private final static Logger logger = LoggerFactory.getLogger(RecoveryIndexServiceImpl.class);
	
	@Autowired
	private IndexLogDao indexLogDao;
	
	@Autowired
	private IndexService indexService;
	
	/**
	 * @see RecoveryIndexService#confirm(String, String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean confirm(String ip, String msg) {
		if(indexLogDao.deleteConfirmLog(msg, ip) > 0) {
			return true;
		} 
		return false;
	}

	/**
	 * @see RecoveryIndexService#commit(String[], String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean commit(String[] ips, String msg) {
		List<IndexLogEntity> list = new ArrayList<>();
		//统计真实的ip个数，防止因为配置文件原因导致的空字符串ip
		int count = 0;
		for(String ip : ips ) {
			if(!"".equals(ip)) {
				count ++;
				IndexLogEntity log = new IndexLogEntity();
				log.setIp(ip);
				log.setMsg(msg);
				String hostIp = IpAddressUtil.getHostIp();
				if(ip.equals(hostIp)) {
					log.setStatus(0);
				}else {
					log.setStatus(1);
				}
				list.add(log);
			}
		}
		//如果插入的日志条数和要同步的ip数相等，则返回真，否则返回false让上层抛出异常进行事务回滚
		if(indexLogDao.bathInsertLog(list) >= count) {
			return true;
		}
		return false;
	}

	/**
	 * @throws IOException 
	 * @see RecoveryIndexService#refresh()
	 */
	@Override
	public void refresh() throws IOException {
		//拿到本机ip
		String hostIp = IpAddressUtil.getHostIp();
		//拿到本机ip需要同步的数据
		List<IndexLogEntity> list = indexLogDao.selectCommit(new IndexLogQuery(hostIp, 1, 0, 1000));
		//逐条同步
		for(IndexLogEntity log : list) {
			String msg = log.getMsg();
			IndexQuery maintainIndexQuery = JSON.parseObject(msg, IndexQuery.class);
			try {
				indexService.addOrUpdateIndex(maintainIndexQuery, false);
				indexLogDao.deleteConfirmLog(msg, hostIp);
			} catch (BusinessException e) {
				logger.error("定时器同步时抛出异常", e);
				String message = e.getMessage();
				//如果同步索引时发现该索引已经插入，则删除日志。其他业务错误则视为索引同步失败，不删除日志
				if("已有该id的索引，无法插入".equals(message)) {
					logger.error("抛出异常后依然执行删除日志操作");
					indexLogDao.deleteConfirmLog(msg, hostIp);
				}
			}
		}
		
	}
	
}
