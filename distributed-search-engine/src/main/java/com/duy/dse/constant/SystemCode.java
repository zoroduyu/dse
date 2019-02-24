package com.duy.dse.constant;

/**
 * 系统结果码枚举类
 *
 * @author wang.zhang
 */
public enum SystemCode {

    /**
     * 业务错误
     */
    E_BUSINESS("100000", "Business Exception"),
    /**
     * 系统错误
     */
    E_ERROR("109999", "error"),
    /**
     * 成功代码
     */
    SUCCESS("000000", "success");

    /**
     * 返回码
     */
    public String code;
    /**
     * 描述
     */
    public String desc;

    SystemCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
