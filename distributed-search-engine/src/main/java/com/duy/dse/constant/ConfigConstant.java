package com.duy.dse.constant;

/**
 * 常量配置
 * @author duyu
 *
 */
public interface ConfigConstant {

	/**
	 * 索引维护的contentType字段值，地址
	 */
	String C_ADDRESS = "ADDRESS";

	/**
	 * 索引维护的contentType字段值，地址
	 */
	String C_RESOURCE = "RESOURCE";

	/**
	 * 索引维护的maintainType字段值，表删除操作
	 */
	String M_DELETE = "DELETE";

	/**
	 * 索引维护的maintainType字段值，表修改操作
	 */
	String M_MODIFY = "MODIFY";

	/**
	 * 索引维护的maintainType字段值，表添加操作
	 */
	String M_ADD = "ADD";

	/**
	 * 线程池维护线程的最小数量. 新增操作在后期应该不是很频繁，所以只维护一个线程，减少资源消耗
	 */
	Integer THREAD_CORE_POOLSIZE = 1;

	/**
	 * 最大线程数，暂时设20
	 */
	Integer THREAD_MAX_POOL_SIZE = 20;

	/**
	 * 等待队列大小
	 */
	Integer THREAD_QUEUE_CAPACITY = 25;
}
