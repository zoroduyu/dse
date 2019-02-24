package com.duy.dse.exception;

/**
 * 自定义业务异常
 *
 * @author wang.zhang
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -2527805798827194493L;

    /**
     * 自定义消息
     */
    private String message;

    /**
     * 构造方法
     *
     * @param message 消息内容
     */
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
