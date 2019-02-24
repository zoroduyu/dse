package com.duy.dse.service;

import java.io.IOException;

/**
 * 保证索引同步一致性接口
 * @author duyu
 *
 */
public interface RecoveryIndexService {

	/**
	 * 以实现最终一致性为目标的索引同步事务接口：
	 * 确认已同步操作
	 * @param ip 要同步的ip
	 * @param msg 这次索引操作的入参报文
	 * @return 返回确认结果
	 */
	boolean confirm(String ip,String msg);
	
	/**
	 * 以实现最终一致性为目标的索引同步事务接口：
	 * 提交待同步事务操作
	 * @param ips 要同步的ips
	 * @param msg 这次索引操作的入参报文
	 * @return 返回待同步事务操作结果
	 */
	boolean commit(String[] ips,String msg);
	
	/**
	 * 以实现最终一致性为目标的索引同步事务接口：
	 * 刷新一致性接口
	 * @throws IOException 
	 */
	void refresh() throws IOException;
}
