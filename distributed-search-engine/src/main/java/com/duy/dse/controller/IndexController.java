package com.duy.dse.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duy.dse.query.IndexQuery;
import com.duy.dse.query.IndexSearchQuery;
import com.duy.dse.service.IndexService;
import com.duy.dse.util.StringUtils;
import com.duy.dse.vo.ResponseVO;

/**
 * 索引维护（接口）
 *
 * @author duyu
 */
@RestController
@RequestMapping("index")
public class IndexController {

	@Autowired
	private IndexService indexService;

	/**
	 * 支持新增、修改、删除索引，并且自动进行索引文件合并。
	 * 
	 * @author duyu
	 * @param maintainIndexQuery 索引维护接口查询的实体类
	 * @return ResponseVO对象,标识成功还是失败
	 * @throws IOException 抛出io异常 2018年10月9日
	 */
	@PostMapping("malongain")
	public ResponseVO<?> maintainIndex(@RequestBody IndexQuery maintainIndexQuery) throws IOException {

		if (StringUtils.blanked((maintainIndexQuery.getContentType()))) {
			return ResponseVO.fail("传入的索引类型为空");
		}
		if (StringUtils.blanked((maintainIndexQuery.getMalongalongype()))) {
			return ResponseVO.fail("传入的操作类型为空");
		}
		// 加入传入的list没有值，则不能做任何操作，直接返回
		if (CollectionUtils.isEmpty(maintainIndexQuery.getIndexs())) {
			return ResponseVO.fail("传入的索引集合为空");
		}

		indexService.addOrUpdateIndex(maintainIndexQuery, true);

		return ResponseVO.success();
	}

	/**
	 * 索引搜索接口
	 * 
	 * @author duyu
	 * @param indexSearchQuery 索引搜索查询实体类
	 * @return ResponseVO对象,标识成功还是失败
	 * @throws IOException    抛出io流异常
	 * @throws ParseException 抛出转换异常
	 */
	@PostMapping("search")
	public ResponseVO<?> search(@RequestBody IndexSearchQuery indexSearchQuery) throws IOException, ParseException {

		// 加入传入的list没有值，则不能做任何操作，直接返回
		if (StringUtils.blanked(indexSearchQuery.getContent())){
			return ResponseVO.fail("传入的查询字段为空");
		}
		if(StringUtils.isSpecialChar(indexSearchQuery.getContent())) {
			return ResponseVO.fail("传入的查询字段含有特殊字符");
		}
		if(indexSearchQuery.getQueryParser() == null || indexSearchQuery.getQueryParser().length <= 0) {
			return ResponseVO.fail("传入的查询字段类型为空");
		}
		if (StringUtils.blanked(indexSearchQuery.getContentType())) {
			return ResponseVO.fail("传入的内容类型为空");
		}
		if(indexSearchQuery.getRows() == null || indexSearchQuery.getRows() <= 0) {
			return ResponseVO.fail("传入的条数不能小于等于0");
		}

		List<Map<String, String>> list = indexService.selectIndexDocmentByConetent(indexSearchQuery);
		if (CollectionUtils.isEmpty(list)) {
			return ResponseVO.fail("查询结果为空");
		}
		return ResponseVO.success(list);
	}

}
