package com.library.interceptor;

import com.library.annotation.RequireAdmin;
import com.library.common.BusinessException;
import com.library.common.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        
        // 检查方法或类上是否有@RequireAdmin注解
        RequireAdmin methodAnnotation = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        RequireAdmin classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        
        if (methodAnnotation == null && classAnnotation == null) {
            return true;
        }
        
        // 检查角色
        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != Constants.Role.ADMIN) {
            throw new BusinessException(403, "无权限访问");
        }
        
        return true;
    }
}
