package com.library.controller;

import com.library.annotation.Log;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.service.AuthService;
import com.library.vo.Result;
import com.library.vo.TokenVO;
import com.library.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户注册
     */
    @Log("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success("注册成功", null);
    }
    
    /**
     * 用户登录
     */
    @Log("用户登录")
    @PostMapping("/login")
    public Result<TokenVO> login(@Valid @RequestBody LoginDTO dto) {
        TokenVO tokenVO = authService.login(dto);
        return Result.success(tokenVO);
    }
    
    /**
     * 用户退出
     */
    @Log("用户退出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            authService.logout(userId);
        }
        return Result.success("退出成功", null);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO userVO = authService.getUserInfo(userId);
        return Result.success(userVO);
    }
}
