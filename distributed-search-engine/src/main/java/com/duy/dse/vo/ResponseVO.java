package com.duy.dse.vo;

import com.duy.dse.constant.SystemCode;

/**
 * 消息响应实体
 *
 * @author wang.zhang
 */
public class ResponseVO<T> {

    /**
     * 响应码
     */
    private String code;
    /**
     * 消息
     */
    private String message;

    /**
     * 消息消息体 有可能是具体的json结果 有可能是一段简单的字符串
     */
    private T data;

    /**
     * 构造方法
     *
     * @param systemCode 系统编码
     * @param message    消息
     * @param data       具体数据
     */
    private ResponseVO(SystemCode systemCode, String message, T data) {
        this.code = systemCode.code;
        this.message = message;
        this.data = data;
    }

    /**
     * 使用此构造方法 代表返回成功
     *
     * @param data 消息体
     */
    private ResponseVO(T data) {
        this.code = SystemCode.SUCCESS.code;
        this.message = SystemCode.SUCCESS.desc;
        this.data = data;
    }

    /**
     * 构建返回对象
     *
     * @param systemCode 返回码
     * @param message    返回消息
     * @param data       消息体
     * @return 响应对象
     */
    public static ResponseVO build(SystemCode systemCode, String message, Object data) {
        return new ResponseVO<>(systemCode, message, data);
    }

    /**
     * 成功消息
     *
     * @param data 具体返回消息
     * @return 响应对象
     */
    public static ResponseVO success(Object data) {
        return new ResponseVO<>(data);
    }

    /**
     * 成功消息
     *
     * @return 响应对象
     */
    public static ResponseVO success() {
        return new ResponseVO<>(null);
    }

    /**
     * 失败消息
     *
     * @param message 消息描述
     * @return 响应对象
     */
    public static ResponseVO fail(String message) {
        return new ResponseVO<>(SystemCode.E_BUSINESS, message, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseVO{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
