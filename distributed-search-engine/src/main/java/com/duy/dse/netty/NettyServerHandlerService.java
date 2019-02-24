package com.duy.dse.netty;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.duy.dse.query.IndexQuery;
import com.duy.dse.service.IndexService;
import com.duy.dse.service.RecoveryIndexService;
import com.duy.dse.util.IpAddressUtil;

/**
 * rpc同步索引文件的具体实现类
 * 
 * @author duyu
 *
 */
@Component
public class NettyServerHandlerService extends NettyServerHandler {

	private final static Logger logger = LoggerFactory.getLogger(NettyServerHandlerService.class);

	@Autowired
	private IndexService indexService;

	@Autowired
	private RecoveryIndexService recoveryIndexService;

	/**
	 * 同步索引文件方法
	 */
	@Override
	protected String dealService(String msg) {
		logger.info("开始同步索引"+msg);
		// 同步索引的操作
		IndexQuery query = JSON.parseObject(msg, IndexQuery.class);
		try {
			String hostIp = IpAddressUtil.getHostIp();
			if (!"".equals(hostIp)) {
				//如果确认事务同步成功
				if (recoveryIndexService.confirm(hostIp, msg)) {
					//则同步索引
					indexService.addOrUpdateIndex(query,false);
				}
			} else {
				logger.info("获取本机ip时出错");
			}
		} catch (IOException e) {
			logger.info("rpc同步索引时出错:{}", e);
		}
		return "";
	}

}
