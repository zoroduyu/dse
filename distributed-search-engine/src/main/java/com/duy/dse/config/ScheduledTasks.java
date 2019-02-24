package com.duy.dse.config;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.duy.dse.core.WriterAndDirManager;
import com.duy.dse.entity.IndexDirEntity;
import com.duy.dse.service.RecoveryIndexService;

/**
 * 定时任务类，用于执行定时任务
 * @author duyu
 *
 */
@Component
public class ScheduledTasks {

	private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private IndexDirEntity indexDir;

	@Autowired
	private WriterAndDirManager writerAndDirManager;
	
	@Autowired
	private RecoveryIndexService recoveryIndexService;

	/**
	 * 每天午夜一点钟将两个索引文件夹合并
	 * 
	 * @throws IOException
	 */
	@Scheduled(cron = "0 0 1 * * ? ")
	public void testCron() {
		try {
			// 分别拿到资源和地址的Directory,IndexReader和IndexWriter对象
			Directory resourceDirectory = writerAndDirManager.getDirectory(indexDir.getResourceDir());
			Directory addressDirectory = writerAndDirManager.getDirectory(indexDir.getAddressDir());
			IndexWriter resourceIndex = writerAndDirManager.getIndexWriter(resourceDirectory);
			IndexWriter addressIndex = writerAndDirManager.getIndexWriter(addressDirectory);

			// 合并提交
			resourceIndex.forceMerge(1);
			addressIndex.forceMerge(1);
			resourceIndex.commit();
			addressIndex.commit();
		} catch (Exception e) {
			logger.error("执行合并索引文件夹任务时抛出异常",e);
		}
	}
	
	/**
	 * 每分钟扫描一次，将索引同步
	 * @throws IOException
	 */
	@Scheduled(cron = "0 */1 * * * ?")
	public void synIndex() throws IOException {
		recoveryIndexService.refresh();
	}

}
