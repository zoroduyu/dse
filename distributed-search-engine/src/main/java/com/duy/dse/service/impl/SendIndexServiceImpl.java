package com.duy.dse.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.duy.dse.config.NettyConfig;
import com.duy.dse.netty.NettyClientRunnable;
import com.duy.dse.service.SendIndexService;
import com.duy.dse.util.IpAddressUtil;

/**
 * 发送同步索引请求的业务实现类
 * 
 * @author duyu
 *
 */
@Service("SendIndexServiceImpl")
public class SendIndexServiceImpl implements SendIndexService {

	@Autowired
	private NettyConfig nettyConfig;

	@Autowired
	private TaskExecutor taskExecutor;

	@Override
	public void sendIndex(String msg) {
		// 拿到要同步的所有服务器ip
		String[] ips = nettyConfig.getIps();
		String hostIp = IpAddressUtil.getHostIp();
		for (String ip : ips) {
			// 多线程异步进行索引同步
			if (!"".equals(ip) && !hostIp.equals(ip)) {
				taskExecutor.execute(new NettyClientRunnable(nettyConfig.getPort(), ip, msg));
			}
		}
	}

}
