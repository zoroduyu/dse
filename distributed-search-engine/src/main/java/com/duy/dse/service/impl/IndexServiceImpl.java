package com.duy.dse.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duy.dse.config.NettyConfig;
import com.duy.dse.constant.ConfigConstant;
import com.duy.dse.core.WriterAndDirManager;
import com.duy.dse.entity.IndexDirEntity;
import com.duy.dse.exception.BusinessException;
import com.duy.dse.query.IndexQuery;
import com.duy.dse.query.IndexSearchQuery;
import com.duy.dse.service.IndexService;
import com.duy.dse.service.RecoveryIndexService;
import com.duy.dse.service.SendIndexService;
import com.duy.dse.util.StringUtils;

/**
 * 资源相关接口实现
 *
 * @author duyu
 */
@Service("indexService")
public class IndexServiceImpl implements IndexService {

	private final static Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

	@Autowired
	private IndexDirEntity indexDir;

	@Autowired
	private WriterAndDirManager writerAndDirManager;

	@Autowired
	private SendIndexService sendIndexService;

	@Autowired
	private RecoveryIndexService recoveryIndexService;

	@Autowired
	private NettyConfig nettyConfig;

	/**
	 * 对索引进行添加，更新和删除的方法
	 * 
	 * @author duyu
	 * @see IndexService#addOrUpdateIndex(String, String, List)
	 */
	@Override
	public void addOrUpdateIndex(IndexQuery maintainIndexQuery, boolean syn) throws IOException {
		String msg = JSON.toJSONString(maintainIndexQuery);
		// 如果要进行索引同步
		if (syn && nettyConfig.isSyned()) {
			// 如果确认发起事务失败，则抛出异常。如果下面写入索引抛出异常或者服务器宕机，commit接口需要保证能进行事务回滚
			if (!recoveryIndexService.commit(nettyConfig.getIps(), msg)) {
				throw new BusinessException("确认同步事务失败!");
			}
		}

		// 得到资源路径
		String path = this.getPathByContentType(maintainIndexQuery.getContentType().toUpperCase());
		List<JSONObject> indexs = maintainIndexQuery.getIndexs();
		// 根据操作的不同，进入调用不同的方法
		switch (maintainIndexQuery.getMalongalongype().toUpperCase()) {
		case ConfigConstant.M_ADD:
			this.addIndex(path, indexs);
		case ConfigConstant.M_MODIFY:
			this.updateIndex(path, indexs);
			break;
		case ConfigConstant.M_DELETE:
			this.deleteIndex(path, indexs);
			break;
		default:
			throw new BusinessException("未传入正确的malongalongype参数");
		}

		// 如果要进行索引同步
		if (syn && nettyConfig.isSyned()) {
			// 进行索引同步操作,因为rpc发起的请求不可以回滚撤回，所以该操作必须放在本地索引已经完成写入之后
			sendIndexService.sendIndex(JSON.toJSONString(maintainIndexQuery));
		}
	}

	/**
	 * 通过contentType得到文件路径的方法
	 * 
	 * @author duyu
	 * @param contentType 内容类型
	 * @return 对应的文件夹路径 2018年10月9日
	 */
	private String getPathByContentType(String contentType) {
		if (StringUtils.blanked((contentType))) {
			throw new BusinessException("contentType不能为空");
		}
		return indexDir.getDir() + "/" + contentType;
	}

	/**
	 * 更新索引的方法
	 * 
	 * @param path   文件夹路径
	 * @param indexs 要添加的索引list对象
	 * @throws IOException io流异常
	 */
	private void updateIndex(String path, List<JSONObject> indexs) throws IOException {
		Directory directory = null;
		IndexWriter indexWriter = null;
		directory = writerAndDirManager.getDirectory(path);
		indexWriter = writerAndDirManager.getIndexWriter(directory);

		// 遍历要更新的索引
		for (JSONObject obj : indexs) {
			// 判断id是否非空
			String id = obj.getString("id");
			if (StringUtils.blanked((id))) {
				throw new BusinessException("传入的id参数中有空值");
			}
			// 查询索引，如果存在则更新，不存在则返回异常
			Term query = this.findIndexById(id, directory);
			// 如果存在 更新
			if (query != null) {
				indexWriter.updateDocument(query, this.addDoc(obj));
			} else {
				throw new BusinessException("未查询到id为" + id + "的索引，无法进行更新操作");
			}
		}
		indexWriter.flush();
		indexWriter.commit();
	}

	/**
	 * 删除索引的方法
	 * 
	 * @param path   文件夹路径
	 * @param indexs 要添加的索引list对象
	 * @throws IOException io流异常
	 */
	private void deleteIndex(String path, List<JSONObject> indexs) throws IOException {
		Directory directory = null;
		IndexWriter indexWriter = null;
		directory = writerAndDirManager.getDirectory(path);
		indexWriter = writerAndDirManager.getIndexWriter(directory);
		for (JSONObject obj : indexs) {
			// 如果id为空 则继续循环
			String id = obj.getString("id");
			if (id == null || id.isEmpty()) {
				continue;
			}

			try {
				Term query = this.findIndexById(id, directory);
				// 如果不等于空 则说明有索引，执行删除操作
				if (query != null) {
					indexWriter.deleteDocuments(query);
				}
			} catch (IOException e1) {
				logger.error("删除操作时查询索引抛出异常", e1);
				continue;
			}
		}
		indexWriter.flush();
		indexWriter.commit();
	}

	/**
	 * 添加索引的方法
	 * 
	 * @param path   文件夹路径
	 * @param indexs 要添加的索引list对象
	 * @throws IOException io流异常
	 */
	private void addIndex(String path, List<JSONObject> indexs) throws IOException {
		Directory targetDirectory = null;
		IndexWriter targetWriter = null;
		// 获取临时和目标文件夹和indexWriter
		targetDirectory = writerAndDirManager.getDirectory(path);
		targetWriter = writerAndDirManager.getIndexWriter(targetDirectory);
		Set<String> idsSet = new HashSet<>();
		// 对传入的索引进行循环添加
		for (JSONObject obj : indexs) {
			// 添加前先判断id非空
			String id = obj.getString("id");
			if (StringUtils.blanked((id))) {
				throw new BusinessException("传入的id为空");
			}
			if (idsSet.contains(id)) {
				throw new BusinessException("传入的id有重复");
			}

			// 去目标文件夹下按照id查询，如果已有该索引则不能添加
			Term query = this.findIndexById(id, targetDirectory);
			if (query != null) {
				throw new BusinessException("已有id为"+id+"的索引，无法插入");
			}

			targetWriter.addDocument(this.addDoc(obj));
			// 将已存入temp文件夹的id存入set中，借此来判断传入的id中是否有重复的
			idsSet.add(id);
		}

		targetWriter.flush();
		targetWriter.commit();
	}

	/**
	 * 通过id去查询记录，查到了返回查询条件，未查到返回空
	 * 
	 * @param id        索引的id字段
	 * @param directory 文件夹字段
	 * @return 返回一个term的查询对象，可以被删除方法用来删除
	 * @throws IOException io流异常
	 */
	private Term findIndexById(String id, Directory directory) throws IOException {
		IndexReader indexReader = null;
		try {
			// 先得到一个indexReader，
			indexReader = DirectoryReader.open(directory);
			// 通过indexSearcher 去检索索引目录...
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			// term 我需要根据那个字段进行检索，字段对应的值...
			Term term = new Term("id", id);
			Query query = new TermQuery(term);

			// 搜索先搜索索引目录..
			// 找到符合query 条件的前面条记录...
			TopDocs topDocs = indexSearcher.search(query, 1);
			// 如果总记录数为空 返回空 否则返回query以供删除
			if (topDocs.totalHits <= 0) {
				return null;
			} else {
				return term;
			}
		} catch (IndexNotFoundException e) {
			// 捕获一个异常 该异常表示没有在目录找到文件，说明是索引文件还未创建 返回空让程序继续往下
			logger.error("该异常表示没有在目录找到文件，说明是索引文件还未创建", e);
			return null;
		} finally {
			// 无论如何都要关掉读取流
			if (indexReader != null) {
				indexReader.close();
			}
		}
	}

	/**
	 * 将JSON映射到文档中
	 * 
	 * @param maintainIndexEntity 索引实体类对象
	 * @return 添加索引的Document
	 */
	private Document addDoc(JSONObject obj) {
		Document document = new Document();
		Set<Entry<String, Object>> entrySet = obj.entrySet();
		Iterator<Entry<String, Object>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> next = iterator.next();
			String key = next.getKey();
			String value = this.returnNullStr(next.getValue());
			if ("id".equals(key)) {
				document.add(new StringField(key, value, Field.Store.YES));
			} else {
				document.add(new TextField(key, value, Field.Store.YES));
			}
		}
		return document;
	}

	/**
	 * 为空则转为空字符串
	 * 
	 * @param str
	 * @return 转换结果
	 */
	private String returnNullStr(Object str) {
		return str == null ? "" : String.valueOf(str);
	}

	/**
	 * 索引搜索接口主方法，用于根据索引查询出匹配的记录
	 * 
	 * @author duyu
	 * @param indexSearchQuery 查询实体类
	 * @return 查询记录的list
	 * @throws IOException    io异常
	 * @throws ParseException 转换异常
	 * @see IndexService#selectIndexDocmentByConetent(IndexSearchQuery)
	 */
	@Override
	public List<Map<String, String>> selectIndexDocmentByConetent(IndexSearchQuery indexSearchQuery)
			throws IOException, ParseException {
		// 先解析path的路径地址
		String path = this.getPathByContentType(indexSearchQuery.getContentType().toUpperCase());

		// 拿到前端传入的字段
		String[] filedStr = indexSearchQuery.getQueryParser();
		// 查询对象
		QueryParser queryParser = new MultiFieldQueryParser(filedStr, writerAndDirManager.getAnalyzer());
		// 用户输入内容
		Query query = queryParser.parse(indexSearchQuery.getContent());
		// 拿到path之后得到indexReader
		List<Document> listDocuments = null;
		IndexReader indexReader = null;
		try {
			indexReader = DirectoryReader.open(writerAndDirManager.getDirectory(path));
			// 查询拿到结果
			listDocuments = this.executeQuery(query, indexSearchQuery.getRows(), indexReader);
		} catch (IndexNotFoundException e) {
			// 捕获一个异常 该异常表示没有在目录找到文件，说明是索引文件还未创建 返回空让程序继续往下
			logger.error("该异常表示没有在目录找到文件，说明是索引文件还未创建", e);
			return null;
		} finally {
			// 无论如何需要关闭读取流
			if (indexReader != null) {
				indexReader.close();
			}
		}
		// 处理返回结果
		return this.getIndexByDocuments(listDocuments);
	}

	/**
	 * 执行查询方法
	 * 
	 * @param query       查询对象
	 * @param rows        要查询的条数
	 * @param indexReader 文件夹读取流
	 * @return Document的list
	 * @throws IOException io异常
	 */
	private List<Document> executeQuery(Query query, Integer rows, IndexReader indexReader) throws IOException {

		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		TopDocs topDocs = indexSearcher.search(query, rows);
		List<Document> list = new ArrayList<>();
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			// 取得对应的文档对象
			Document document = indexSearcher.doc(scoreDoc.doc);
			list.add(document);
		}

		return list;
	}

	/**
	 * 将doc字段映射到实体bean
	 * 
	 * @param listDocuments 返回MaintainIndexEntity的list
	 * @return 返回IndexEntity的list
	 */
	private List<Map<String,String>> getIndexByDocuments(List<Document> listDocuments) {
		List<Map<String,String>> list = new ArrayList<>();
		for (Document document : listDocuments) {
			Iterator<IndexableField> iterator = document.iterator();
			Map<String,String> map = new HashMap<String, String>();
			while (iterator.hasNext()) {
				IndexableField next = iterator.next();
				String key = next.name();
				String value = next.stringValue();
				map.put(key, value);
			}
			list.add(map);
		}
		return list;
	}

}
