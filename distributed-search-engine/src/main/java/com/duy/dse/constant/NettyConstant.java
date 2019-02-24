package com.duy.dse.constant;
import org.springframework.stereotype.Component;

/**
 * @author duyu
 */
@Component
public interface NettyConstant {

    /**
     * 索引同步结果：失败
     */
    String INDEX_SYC_FAIL = "fail";

    /**
     * 索引同步结果：成功
     */
    String INDEX_SYC_SUCCESS = "success";
}