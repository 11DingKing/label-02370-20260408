package com.library.interceptor;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取Token
        String authHeader = request.getHeader(Constants.TOKEN_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(Constants.TOKEN_PREFIX)) {
            throw new BusinessException(401, "未登录或Token已过期");
        }
        
        String token = authHeader.substring(Constants.TOKEN_PREFIX.length());
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "Token无效或已过期");
        }
        
        // 将用户信息存入request
        request.setAttribute("userId", jwtUtil.getUserId(token));
        request.setAttribute("username", jwtUtil.getUsername(token));
        request.setAttribute("role", jwtUtil.getRole(token));
        
        return true;
    }
}
