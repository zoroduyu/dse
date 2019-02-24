package com.duy.dse.core;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

/**
 * 用于管理indexWriter和Directory的管理类，所有的indexWirter和Directory都在这里做管理
 * 理论上来讲，一个文件夹只需要一个Directory和indexWriter就够了，不需要重复new对象
 * address和resource有至少两个文件夹，这里map来存储和管理
 * 
 * @author duyu
 *
 */
@Component
public class WriterAndDirManager {
	
	/**
	 *中文分词器
	 */
	private	SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();

	/**
	 * 存储indexWriter的map
	 */
	private Map<Directory, IndexWriter> writerMap = new ConcurrentHashMap<>();

	/**
	 * 存储Directory的map
	 */
	private Map<String, Directory> dirMap = new ConcurrentHashMap<>();

	/**
	 * 获取Directory
	 * 
	 * @param path 传入文件夹路径
	 * @return 返回一个Directory对象
	 * @throws IOException
	 */
	public Directory getDirectory(String path) throws IOException {
		// 如果map中有则直接返回
		if (dirMap.containsKey(path)) {
			return dirMap.get(path);
		}
		// 没有则new 一个加入map
		Directory directory = FSDirectory.open(Paths.get(path));
		dirMap.put(path, directory);
		return directory;
	}

	/**
	 * 得到IndexWriter对象
	 * @param directory 传入directory文件夹对象
	 * @return 返回可以使用，没有被close掉的IndexWriter对象
	 * @throws IOException
	 */
	public IndexWriter getIndexWriter(Directory directory) throws IOException {
		// 如果map中有则直接返回
		if (writerMap.containsKey(directory)) {
			IndexWriter indexWriter = writerMap.get(directory);
			if (indexWriter.isOpen()) {
				return indexWriter;
			}
		}
		// 索引配置
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// 获取索引实例
		IndexWriter indexWriter = new IndexWriter(directory, config);
		// 没有则new一个加入map
		writerMap.put(directory, indexWriter);
		return indexWriter;
	}

	/**
	 * 返回分词器
	 * @return
	 */
	public SmartChineseAnalyzer getAnalyzer() {
		return analyzer;
	}
	
}
