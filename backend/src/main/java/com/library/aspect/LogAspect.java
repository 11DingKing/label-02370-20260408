package com.library.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.annotation.Log;
import com.library.entity.OperationLog;
import com.library.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    
    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;
    
    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        OperationLog operationLog = new OperationLog();
        operationLog.setOperation(logAnnotation.value());
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operationLog.setIp(getIpAddress(request));
            
            Long userId = (Long) request.getAttribute("userId");
            String username = (String) request.getAttribute("username");
            operationLog.setUserId(userId);
            operationLog.setUsername(username);
        }
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        String className = point.getTarget().getClass().getName();
        String methodName = signature.getName();
        operationLog.setMethod(className + "." + methodName);
        
        // 获取参数
        try {
            Object[] args = point.getArgs();
            String params = objectMapper.writeValueAsString(args);
            operationLog.setParams(params.length() > 2000 ? params.substring(0, 2000) : params);
        } catch (Exception e) {
            log.warn("序列化参数失败", e);
        }
        
        Object result = null;
        try {
            result = point.proceed();
            operationLog.setStatus(1);
        } catch (Throwable e) {
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            operationLog.setCostTime(System.currentTimeMillis() - startTime);
            try {
                operationLogMapper.insert(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
        
        return result;
    }
    
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
