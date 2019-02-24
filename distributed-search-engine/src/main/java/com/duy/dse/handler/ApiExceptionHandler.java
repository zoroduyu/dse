package com.duy.dse.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.duy.dse.constant.SystemCode;
import com.duy.dse.exception.BusinessException;
import com.duy.dse.vo.ResponseVO;

/**
 * API异常处理
 *
 * @author wang.zhang
 */
@ControllerAdvice
public class ApiExceptionHandler {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * 参数为空异常
     *
     * @param e 异常类
     * @return 异常消息
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseVO handle(MissingServletRequestParameterException e) {
        LOGGER.error(e.getParameterName() + "参数为空", e);
        return ResponseVO.fail("miss parameter " + e.getParameterName() + ":" + e.getParameterType());
    }

    /**
     * 参数类型不匹配异常
     *
     * @param e 异常类型
     * @return 异常消息
     */
    @ExceptionHandler(value = TypeMismatchException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseVO handle(TypeMismatchException e) {
        LOGGER.error("参数类型错误", e);
        return ResponseVO.fail("required type: " + e.getRequiredType() + ",but value:" + e.getValue());
    }

    /**
     * 请求方式异常处理
     *
     * @param e 异常类
     * @return 异常消息
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseVO handler(HttpRequestMethodNotSupportedException e) {
        LOGGER.error("错误的请求方式", e);
        return ResponseVO.fail("错误的请求方式");
    }

    /**
     * 业务异常处理
     *
     * @param e 异常类
     * @return 异常消息
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public ResponseVO handler(BusinessException e) {
        LOGGER.error(e.getMessage());
        return ResponseVO.fail(e.getMessage());
    }

    /**
     * 未知异常处理
     *
     * @param e 异常类
     * @return 异常消息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseVO handle(Exception e) {
        LOGGER.error("异常处理", e);
        return ResponseVO.build(SystemCode.E_ERROR, "系统异常，请联系关联员", null);
    }
}
