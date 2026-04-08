package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.dto.LoginDTO;
import com.library.dto.RegisterDTO;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.AuthService;
import com.library.util.JwtUtil;
import com.library.util.PasswordEncoder;
import com.library.vo.TokenVO;
import com.library.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void register(RegisterDTO dto) {
        log.info("用户注册: {}", dto.getUsername());
        
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(Constants.Role.USER);
        user.setStatus(Constants.UserStatus.ENABLED);
        
        userMapper.insert(user);
        log.info("用户注册成功: {}", dto.getUsername());
    }
    
    @Override
    public TokenVO login(LoginDTO dto) {
        log.info("用户登录: {}", dto.getUsername());
        
        // 查询用户
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查状态
        if (user.getStatus() == Constants.UserStatus.DISABLED) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        
        log.info("用户登录成功: {}", dto.getUsername());
        return new TokenVO(token, userVO);
    }
    
    @Override
    public void logout(Long userId) {
        log.info("用户退出: {}", userId);
        // JWT无状态，客户端删除Token即可
    }
    
    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
