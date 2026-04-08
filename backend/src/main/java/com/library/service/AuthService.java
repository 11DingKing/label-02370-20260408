package com.library.service;

import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.vo.TokenVO;
import com.library.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    void register(RegisterDTO dto);
    
    /**
     * 用户登录
     */
    TokenVO login(LoginDTO dto);
    
    /**
     * 用户退出
     */
    void logout(Long userId);
    
    /**
     * 获取当前用户信息
     */
    UserVO getUserInfo(Long userId);
}
