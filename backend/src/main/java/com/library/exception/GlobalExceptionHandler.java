package com.library.exception;

import com.library.common.BusinessException;
import com.library.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }
    
    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.isEmpty() ? "参数校验失败" : fieldErrors.get(0).getDefaultMessage();
        log.warn("参数校验失败: field={}, message={}", 
            fieldErrors.isEmpty() ? "unknown" : fieldErrors.get(0).getField(), message);
        return Result.error(400, message);
    }
    
    /**
     * 绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = fieldErrors.isEmpty() ? "参数绑定失败" : fieldErrors.get(0).getDefaultMessage();
        log.warn("参数绑定失败: field={}, message={}", 
            fieldErrors.isEmpty() ? "unknown" : fieldErrors.get(0).getField(), message);
        return Result.error(400, message);
    }
    
    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
