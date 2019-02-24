package com.duy.dse.service;

/**
 * 向各个服务器发送同步索引的rpc请求
 * @author duyu
 *
 */
public interface SendIndexService {

	/**
	 * 发送索引进行同步
	 * @param indexs
	 */
	void sendIndex(String indexs);
}
